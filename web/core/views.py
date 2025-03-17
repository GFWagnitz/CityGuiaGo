from rest_framework import generics, status
from .models import User, Categorias, Atracoes, Roteiros, Avaliacoes, Ofertas, Denuncias, Imagens, RoteiroAtracao
from .serializers import (UserSerializer, LoginSerializer, CategoriasSerializer, AtracoesSerializer,
                          RoteirosSerializer, AvaliacoesSerializer, OfertasSerializer,
                          DenunciasSerializer, ImagensSerializer, RoteiroAtracaoSerializer)
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.authtoken.models import Token
from rest_framework.response import Response
from rest_framework.views import APIView
from django.contrib.auth import authenticate
from django.shortcuts import get_object_or_404
from django.core.exceptions import PermissionDenied
from django.db import models
from rest_framework import serializers
import os
import uuid
import zipfile
import tempfile
from django.shortcuts import render, redirect
from django.contrib.admin.views.decorators import staff_member_required
from django.conf import settings
from django.contrib import messages
from .forms import ImportAtracoesForm
from .management.commands.import_atracoes import Command

class SignupView(generics.CreateAPIView):
    serializer_class = UserSerializer
    permission_classes = [AllowAny]
    
    def post(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            token, created = Token.objects.get_or_create(user=user)
            return Response({
                'user': UserSerializer(user).data,
                'token': token.key
            }, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class LoginView(APIView):
    permission_classes = [AllowAny]
    serializer_class = LoginSerializer
    
    def post(self, request):
        serializer = LoginSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.validated_data['user']
            token, created = Token.objects.get_or_create(user=user)
            return Response({
                'user': UserSerializer(user).data,
                'token': token.key
            })
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class TokenValidateView(APIView):
    permission_classes = [IsAuthenticated]
    
    def get(self, request):
        return Response({
            'user': UserSerializer(request.user).data,
            'valid': True
        })

class UsuarioSelf(generics.RetrieveAPIView):
    serializer_class = UserSerializer
    permission_classes = [IsAuthenticated]  

    def get_object(self):
        return self.request.user
    
    def get(self, request, *args, **kwargs):
        user = self.get_object()
        serializer = self.get_serializer(user)
        return Response({
            'user': serializer.data,
            'is_authenticated': True
        })

class CategoriaList(generics.ListAPIView):
    queryset = Categorias.objects.all()
    serializer_class = CategoriasSerializer
    permission_classes = [AllowAny]

class CategoriaDetail(generics.RetrieveAPIView):
    queryset = Categorias.objects.all()
    serializer_class = CategoriasSerializer
    permission_classes = [AllowAny]
    lookup_field = 'id'

class AtracaoList(generics.ListAPIView):
    queryset = Atracoes.objects.all()
    serializer_class = AtracoesSerializer
    permission_classes = [AllowAny]
    
    def get_serializer_context(self):
        context = super().get_serializer_context()
        return context

class AtracaoDetail(generics.RetrieveAPIView):
    queryset = Atracoes.objects.all()
    serializer_class = AtracoesSerializer
    permission_classes = [AllowAny]
    lookup_field = 'id'
    
    def get_serializer_context(self):
        context = super().get_serializer_context()
        return context

class RoteiroList(generics.ListCreateAPIView):
    serializer_class = RoteirosSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        # Show all public roteiros and user's own roteiros
        return Roteiros.objects.filter(public=True) | Roteiros.objects.filter(user=self.request.user)
    
    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class RoteiroDetail(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = RoteirosSerializer
    permission_classes = [IsAuthenticated]
    lookup_field = 'id'

    def get_queryset(self):
        # Show all public roteiros and user's own roteiros
        return Roteiros.objects.filter(public=True) | Roteiros.objects.filter(user=self.request.user)
    
    def perform_update(self, serializer):
        # Ensure only the owner can update
        roteiro = self.get_object()
        if roteiro.user != self.request.user:
            raise PermissionDenied("You don't have permission to edit this roteiro")
        serializer.save()
    
    def perform_destroy(self, instance):
        # Ensure only the owner can delete
        if instance.user != self.request.user:
            raise PermissionDenied("You don't have permission to delete this roteiro")
        instance.delete()

class RoteiroAtracaoList(generics.ListCreateAPIView):
    serializer_class = RoteiroAtracaoSerializer
    permission_classes = [IsAuthenticated]
    
    def get_queryset(self):
        roteiro_id = self.kwargs.get('roteiro_id')
        roteiro = get_object_or_404(Roteiros, id=roteiro_id)
        
        # Ensure user has access to this roteiro
        if not roteiro.public and roteiro.user != self.request.user:
            return RoteiroAtracao.objects.none()
            
        return RoteiroAtracao.objects.filter(roteiro_id=roteiro_id).order_by('ordem')
    
    def perform_create(self, serializer):
        roteiro_id = self.kwargs.get('roteiro_id')
        roteiro = get_object_or_404(Roteiros, id=roteiro_id)
        
        # Ensure only the owner can add attractions
        if roteiro.user != self.request.user:
            raise PermissionDenied("You don't have permission to add attractions to this roteiro")
        
        # Get the maximum order value or default to 0
        max_ordem = RoteiroAtracao.objects.filter(roteiro=roteiro).aggregate(
            models.Max('ordem'))['ordem__max'] or 0
        
        # Set the ordem value to max + 1 if not provided
        ordem = serializer.validated_data.get('ordem', max_ordem + 1)
        
        # Make sure dia is within roteiro duration
        dia = serializer.validated_data.get('dia', 1)
        if roteiro.duracao and dia > roteiro.duracao:
            raise serializers.ValidationError(
                {"dia": f"Day must be within roteiro duration (1-{roteiro.duracao})"}
            )
        
        serializer.save(roteiro=roteiro, ordem=ordem, dia=dia)

class RoteiroAtracaoDetail(generics.RetrieveUpdateDestroyAPIView):
    serializer_class = RoteiroAtracaoSerializer
    permission_classes = [IsAuthenticated]
    lookup_field = 'id'
    
    def get_queryset(self):
        roteiro_id = self.kwargs.get('roteiro_id')
        return RoteiroAtracao.objects.filter(roteiro_id=roteiro_id)
    
    def perform_update(self, serializer):
        roteiro_atracao = self.get_object()
        roteiro = roteiro_atracao.roteiro
        
        # Ensure only the owner can update
        if roteiro.user != self.request.user:
            raise PermissionDenied("You don't have permission to modify this roteiro")
        
        # Make sure dia is within roteiro duration
        dia = serializer.validated_data.get('dia', roteiro_atracao.dia)
        if roteiro.duracao and dia > roteiro.duracao:
            raise serializers.ValidationError(
                {"dia": f"Day must be within roteiro duration (1-{roteiro.duracao})"}
            )
        
        serializer.save()
    
    def perform_destroy(self, instance):
        roteiro = instance.roteiro
        
        # Ensure only the owner can delete
        if roteiro.user != self.request.user:
            raise PermissionDenied("You don't have permission to modify this roteiro")
        
        instance.delete()

class AvaliacaoList(generics.ListAPIView):
    queryset = Avaliacoes.objects.all()
    serializer_class = AvaliacoesSerializer
    permission_classes = [AllowAny]

class AvaliacaoDetail(generics.RetrieveAPIView):
    queryset = Avaliacoes.objects.all()
    serializer_class = AvaliacoesSerializer
    permission_classes = [AllowAny]
    lookup_field = 'id'

class OfertaList(generics.ListAPIView):
    queryset = Ofertas.objects.all()
    serializer_class = OfertasSerializer
    permission_classes = [AllowAny]

class OfertaDetail(generics.RetrieveAPIView):
    queryset = Ofertas.objects.all()
    serializer_class = OfertasSerializer
    permission_classes = [AllowAny]
    lookup_field = 'id'

class DenunciaList(generics.ListAPIView):
    queryset = Denuncias.objects.all()
    serializer_class = DenunciasSerializer
    permission_classes = [AllowAny]

class DenunciaDetail(generics.RetrieveAPIView):
    queryset = Denuncias.objects.all()
    serializer_class = DenunciasSerializer
    permission_classes = [AllowAny]
    lookup_field = 'id'

class ImagemList(generics.ListAPIView):
    queryset = Imagens.objects.all()
    serializer_class = ImagensSerializer
    permission_classes = [AllowAny]

class ImagemDetail(generics.RetrieveAPIView):
    queryset = Imagens.objects.all()
    serializer_class = ImagensSerializer
    permission_classes = [AllowAny]
    lookup_field = 'id'

@staff_member_required
def import_atracoes_view(request):
    if request.method == 'POST':
        try:
            form = ImportAtracoesForm(request.POST, request.FILES)
            if form.is_valid():
                # Process the form
                csv_file = request.FILES['csv_file']
                images_zip = request.FILES['images_zip']
                encoding = form.cleaned_data.get('encoding') or 'utf-8'
                delimiter = form.cleaned_data.get('delimiter') or ','
                skip_header = form.cleaned_data.get('skip_header')
                user = form.cleaned_data.get('user')
                
                # Check file sizes
                max_csv_size = 10 * 1024 * 1024  # 10MB
                max_zip_size = 100 * 1024 * 1024  # 100MB
                
                if csv_file.size > max_csv_size:
                    messages.error(request, f"CSV file is too large. Maximum size is {max_csv_size/1024/1024}MB.")
                    return render(request, 'admin/core/import_atracoes.html', {'form': form})
                
                if images_zip.size > max_zip_size:
                    messages.error(request, f"ZIP file is too large. Maximum size is {max_zip_size/1024/1024}MB.")
                    return render(request, 'admin/core/import_atracoes.html', {'form': form})
                
                # Create temporary directory for extracted files
                with tempfile.TemporaryDirectory() as temp_dir:
                    try:
                        # Save CSV file
                        csv_file_path = os.path.join(temp_dir, 'import.csv')
                        with open(csv_file_path, 'wb') as f:
                            for chunk in csv_file.chunks():
                                f.write(chunk)
                        
                        # Extract ZIP file
                        images_folder = os.path.join(temp_dir, 'images')
                        os.makedirs(images_folder, exist_ok=True)
                        
                        try:
                            with zipfile.ZipFile(images_zip) as zip_ref:
                                # Check for zip bombs and oversized files
                                total_size = sum(info.file_size for info in zip_ref.infolist())
                                if total_size > max_zip_size * 2:  # Sanity check for decompression
                                    messages.error(request, "ZIP file contents are too large when extracted.")
                                    return render(request, 'admin/core/import_atracoes.html', {'form': form})
                                
                                # Extract only image files
                                allowed_extensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp']
                                for file_info in zip_ref.infolist():
                                    # Skip directories
                                    if file_info.filename.endswith('/'):
                                        continue
                                    
                                    # Check if the file is an image
                                    extension = os.path.splitext(file_info.filename)[1].lower()
                                    if extension in allowed_extensions:
                                        # Extract only the filename, not the path
                                        filename = os.path.basename(file_info.filename)
                                        source = zip_ref.open(file_info)
                                        target = open(os.path.join(images_folder, filename), "wb")
                                        with source, target:
                                            target.write(source.read())
                        except zipfile.BadZipFile:
                            messages.error(request, "The uploaded file is not a valid ZIP file.")
                            return render(request, 'admin/core/import_atracoes.html', {'form': form})
                        
                        # Check if the images directory is not empty
                        if not os.listdir(images_folder):
                            messages.warning(request, "Warning: The ZIP file did not contain any valid image files.")
                        
                        # Run import command with a smaller batch size to prevent timeout
                        import_command = Command()
                        options = {
                            'csv_file': csv_file_path,
                            'images_folder': images_folder,
                            'user': user.username if user else None,
                            'encoding': encoding,
                            'delimiter': delimiter,
                            'skip_header': skip_header,
                        }
                        
                        try:
                            import_command.handle(**options)
                            messages.success(request, "Attractions imported successfully.")
                            return redirect('admin:core_atracoes_changelist')
                        except Exception as e:
                            messages.error(request, f"Error importing attractions: {str(e)}")
                            import traceback
                            error_details = traceback.format_exc()
                            messages.error(request, f"Error details: {error_details}")
                    except Exception as e:
                        messages.error(request, f"Error processing files: {str(e)}")
            else:
                # Form is not valid, display errors
                for field, errors in form.errors.items():
                    for error in errors:
                        messages.error(request, f"Error in {field}: {error}")
        except Exception as e:
            messages.error(request, f"Unexpected error: {str(e)}")
            import traceback
            error_details = traceback.format_exc()
            messages.error(request, f"Error details: {error_details}")
    else:
        form = ImportAtracoesForm()
    
    return render(request, 'admin/core/import_atracoes.html', {'form': form})