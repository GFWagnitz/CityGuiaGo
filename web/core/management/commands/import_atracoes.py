import csv
import os
import uuid
from django.core.management.base import BaseCommand, CommandError
from django.db import transaction
from django.utils import timezone
from core.models import Atracoes, Categorias, Imagens, User
from django.conf import settings


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
        
        # Create media path for images if it doesn't exist
        media_path = os.path.join(settings.MEDIA_ROOT, 'atracoes')
        os.makedirs(media_path, exist_ok=True)

        # Import data
        success_count = 0
        error_count = 0
        
        with open(csv_file_path, 'r', encoding=encoding) as f:
            reader = csv.reader(f, delimiter=delimiter)
            
            # Skip header if specified
            if skip_header:
                next(reader)
            
            for row_num, row in enumerate(reader, start=1):
                try:
                    with transaction.atomic():
                        # Parse CSV row
                        # Expected columns in CSV based on our Atracoes model 
                        # (adjusting based on your specific CSV template)
                        # 0: nome, 1: descricao, 2: categoria_nome, 3: horario_funcionamento, 4: preco_medio,
                        # 5: endereco_logradouro, 6: endereco_numero, 7: endereco_complemento, 
                        # 8: endereco_bairro, 9: endereco_cidade, 10: endereco_estado, 
                        # 11: endereco_cep, 12: endereco_coordenadas, 13: imagens_list (comma-separated filenames)
                        
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
                            
                            for image_filename in image_filenames:
                                source_image_path = os.path.join(images_folder, image_filename)
                                
                                if not os.path.isfile(source_image_path):
                                    self.stderr.write(f'Image file not found: {source_image_path}')
                                    continue
                                
                                # Generate a unique filename
                                file_ext = os.path.splitext(image_filename)[1].lower()
                                unique_filename = f"{uuid.uuid4()}{file_ext}"
                                dest_path = os.path.join('atracoes', unique_filename)
                                
                                # Copy the file to media directory
                                dest_image_path = os.path.join(settings.MEDIA_ROOT, dest_path)
                                os.makedirs(os.path.dirname(dest_image_path), exist_ok=True)
                                
                                with open(source_image_path, 'rb') as src, open(dest_image_path, 'wb') as dst:
                                    dst.write(src.read())
                                
                                # Create Imagens record
                                Imagens.objects.create(
                                    atracao=atracao,
                                    user=user,
                                    caminho=os.path.join(settings.MEDIA_URL, dest_path).lstrip('/'),
                                    created_at=timezone.now()
                                )
                        
                        success_count += 1
                        self.stdout.write(f'Successfully imported attraction: {atracao.nome}')
                
                except Exception as e:
                    error_count += 1
                    self.stderr.write(f'Error importing row {row_num}: {e}')
        
        self.stdout.write(self.style.SUCCESS(
            f'Import complete. Successfully imported {success_count} attractions. '
            f'Encountered {error_count} errors.'
        )) 