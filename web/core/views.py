from rest_framework import generics
from .models import Usuarios, Categorias, Atracoes, Roteiros, Avaliacoes, Ofertas, Denuncias, Imagens
from .serializers import (UsuariosSerializer, CategoriasSerializer, AtracoesSerializer,
                          RoteirosSerializer, AvaliacoesSerializer, OfertasSerializer,
                          DenunciasSerializer, ImagensSerializer)
from rest_framework.permissions import IsAuthenticated, AllowAny

class UsuarioSelf(generics.RetrieveAPIView):
    serializer_class = UsuariosSerializer
    permission_classes = [IsAuthenticated]  

    def get_object(self):
        return self.request.user 
    
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