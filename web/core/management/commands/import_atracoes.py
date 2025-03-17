import csv
import os
import uuid
import shutil
from django.core.management.base import BaseCommand, CommandError
from django.db import transaction
from django.utils import timezone
from core.models import Atracoes, Categorias, Imagens, User
from django.conf import settings
import logging

logger = logging.getLogger(__name__)

class Command(BaseCommand):
    help = 'Import attractions from a CSV file and associated images from a folder'

    def add_arguments(self, parser):
        parser.add_argument('csv_file', type=str, help='Path to the CSV file containing attraction data')
        parser.add_argument('images_folder', type=str, help='Path to the folder containing images')
        parser.add_argument('--user', type=str, help='Username of the user to associate with uploaded images', default=None)
        parser.add_argument('--encoding', type=str, help='CSV file encoding', default='utf-8')
        parser.add_argument('--delimiter', type=str, help='CSV delimiter', default=',')
        parser.add_argument('--skip-header', action='store_true', help='Skip the header row in the CSV file')

    def handle(self, *args, **options):
        csv_file_path = options['csv_file']
        images_folder = options['images_folder']
        username = options['user']
        encoding = options['encoding']
        delimiter = options['delimiter']
        skip_header = options['skip_header']

        # Check if CSV file exists
        if not os.path.isfile(csv_file_path):
            raise CommandError(f'CSV file {csv_file_path} does not exist')

        # Check if images folder exists
        if not os.path.isdir(images_folder):
            raise CommandError(f'Images folder {images_folder} does not exist')

        # Get user if specified
        user = None
        if username:
            try:
                user = User.objects.get(username=username)
                self.stdout.write(f'Using user: {user.username}')
            except User.DoesNotExist:
                raise CommandError(f'User with username {username} does not exist')
        
        # Ensure media directory exists - create one if not configured
        media_root = getattr(settings, 'MEDIA_ROOT', '')
        if not media_root:
            # Use a default directory in the project root
            base_dir = getattr(settings, 'BASE_DIR', os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
            media_root = os.path.join(base_dir, 'media')
            self.stdout.write(f'MEDIA_ROOT not configured, using default: {media_root}')
        
        # Create media path for images if it doesn't exist
        atracoes_media_path = os.path.join(media_root, 'atracoes')
        os.makedirs(atracoes_media_path, exist_ok=True)
        self.stdout.write(f'Using media path: {atracoes_media_path}')

        # Get media URL (default to /media/ if not set)
        media_url = getattr(settings, 'MEDIA_URL', '/media/')
        if not media_url.endswith('/'):
            media_url += '/'
        
        # Import data
        success_count = 0
        error_count = 0
        
        try:
            with open(csv_file_path, 'r', encoding=encoding) as f:
                reader = csv.reader(f, delimiter=delimiter)
                
                # Skip header if specified
                if skip_header:
                    next(reader)
                
                total_rows = sum(1 for _ in reader)
                f.seek(0)  # Reset file position
                if skip_header:
                    next(reader)
                
                self.stdout.write(f'Found {total_rows} rows in CSV file')
                
                for row_num, row in enumerate(reader, start=1):
                    if row_num % 5 == 0:
                        self.stdout.write(f'Processing row {row_num}/{total_rows}')
                        
                    try:
                        with transaction.atomic():
                            # Skip empty rows
                            if not row or not row[0].strip():
                                continue
                                
                            # Basic validation
                            if len(row) < 13:
                                self.stderr.write(f'Row {row_num} has insufficient columns: {len(row)}')
                                error_count += 1
                                continue
                            
                            # Get or create categoria
                            categoria_nome = row[2].strip()
                            try:
                                categoria = Categorias.objects.get(descricao=categoria_nome)
                            except Categorias.DoesNotExist:
                                self.stdout.write(f'Category {categoria_nome} not found, creating it')
                                categoria = Categorias.objects.create(
                                    descricao=categoria_nome,
                                    created_at=timezone.now()
                                )
                            
                            # Handle price conversion
                            preco_medio = None
                            if row[4].strip():
                                try:
                                    preco_medio = float(row[4].strip().replace(',', '.'))
                                except ValueError:
                                    self.stderr.write(f'Invalid price in row {row_num}: {row[4]}')
                            
                            # Create atracao
                            atracao = Atracoes.objects.create(
                                nome=row[0].strip(),
                                descricao=row[1].strip(),
                                categoria=categoria,
                                horario_funcionamento=row[3].strip() or None,
                                preco_medio=preco_medio,
                                endereco_logradouro=row[5].strip() or None,
                                endereco_numero=row[6].strip() or None,
                                endereco_complemento=row[7].strip() or None,
                                endereco_bairro=row[8].strip() or None,
                                endereco_cidade=row[9].strip() or None,
                                endereco_estado=row[10].strip() or None,
                                endereco_cep=row[11].strip() or None,
                                endereco_coordenadas=row[12].strip() or None,
                                created_at=timezone.now()
                            )
                            
                            # Process images if column exists
                            if len(row) > 13 and row[13].strip():
                                image_filenames = [f.strip() for f in row[13].split(';') if f.strip()]
                                
                                for image_idx, image_filename in enumerate(image_filenames):
                                    source_image_path = os.path.join(images_folder, image_filename)
                                    
                                    if not os.path.isfile(source_image_path):
                                        self.stderr.write(f'Image file not found: {source_image_path}')
                                        continue
                                    
                                    try:
                                        # Generate a unique filename
                                        file_ext = os.path.splitext(image_filename)[1].lower()
                                        unique_filename = f"{uuid.uuid4()}{file_ext}"
                                        dest_rel_path = os.path.join('atracoes', unique_filename)
                                        dest_abs_path = os.path.join(media_root, dest_rel_path)
                                        
                                        # Ensure directory exists
                                        os.makedirs(os.path.dirname(dest_abs_path), exist_ok=True)
                                        
                                        # Copy the file
                                        shutil.copy2(source_image_path, dest_abs_path)
                                        self.stdout.write(f'Copied image to {dest_abs_path}')
                                        
                                        # Build the URL path
                                        url_path = f"{media_url.rstrip('/')}/{dest_rel_path}"
                                        
                                        # Create imagem record
                                        imagem = Imagens.objects.create(
                                            atracao=atracao,
                                            user=user,
                                            caminho=url_path,
                                            created_at=timezone.now()
                                        )
                                        
                                        self.stdout.write(f'Created image record with path: {imagem.caminho}')
                                    except Exception as img_e:
                                        self.stderr.write(f'Error processing image {image_filename}: {img_e}')
                                        import traceback
                                        self.stderr.write(traceback.format_exc())
                            
                            success_count += 1
                            self.stdout.write(f'Successfully imported attraction: {atracao.nome}')
                    
                    except Exception as e:
                        error_count += 1
                        self.stderr.write(f'Error importing row {row_num}: {e}')
                        import traceback
                        self.stderr.write(traceback.format_exc())
            
            self.stdout.write(self.style.SUCCESS(
                f'Import complete. Successfully imported {success_count} attractions. '
                f'Encountered {error_count} errors.'
            ))
        
        except Exception as e:
            self.stderr.write(f'Fatal error during import: {e}')
            import traceback
            self.stderr.write(traceback.format_exc())
            raise CommandError('Import failed') 