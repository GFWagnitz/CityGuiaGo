from rest_framework import generics, status
from .models import User, Categorias, Atracoes, Roteiros, Avaliacoes, Ofertas, Denuncias, Imagens
from .serializers import (UserSerializer, LoginSerializer, CategoriasSerializer, AtracoesSerializer,
                          RoteirosSerializer, AvaliacoesSerializer, OfertasSerializer,
                          DenunciasSerializer, ImagensSerializer)
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.authtoken.models import Token
from rest_framework.response import Response
from rest_framework.views import APIView
from django.contrib.auth import authenticate

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

class AtracaoDetail(generics.RetrieveAPIView):
    queryset = Atracoes.objects.all()
    serializer_class = AtracoesSerializer
    permission_classes = [AllowAny]
    lookup_field = 'id'

class RoteiroList(generics.ListAPIView):
    queryset = Roteiros.objects.all()
    serializer_class = RoteirosSerializer
    permission_classes = [AllowAny]

class RoteiroDetail(generics.RetrieveAPIView):
    queryset = Roteiros.objects.all()
    serializer_class = RoteirosSerializer
    permission_classes = [AllowAny]
    lookup_field = 'id'

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