from django.urls import path
from . import views

urlpatterns = [
    # Authentication endpoints
    path('auth/signup/', views.SignupView.as_view(), name='auth-signup'),
    path('auth/login/', views.LoginView.as_view(), name='auth-login'),
    path('auth/validate/', views.TokenValidateView.as_view(), name='auth-validate'),
    
    path('me/', views.UsuarioSelf.as_view(), name='usuario-self'), #TODO: Corrigir usuario e user nao sao iguais

    path('categorias/', views.CategoriaList.as_view(), name='categoria-list'),
    path('categorias/<uuid:id>/', views.CategoriaDetail.as_view(), name='categoria-detail'),

    path('atracoes/', views.AtracaoList.as_view(), name='atracao-list'),
    path('atracoes/<uuid:id>/', views.AtracaoDetail.as_view(), name='atracao-detail'),

    path('roteiros/', views.RoteiroList.as_view(), name='roteiro-list'),
    path('roteiros/<uuid:id>/', views.RoteiroDetail.as_view(), name='roteiro-detail'),
    
    # Roteiro-Atracao relationship endpoints
    path('roteiros/<uuid:roteiro_id>/atracoes/', views.RoteiroAtracaoList.as_view(), name='roteiro-atracao-list'),
    path('roteiros/<uuid:roteiro_id>/atracoes/<uuid:id>/', views.RoteiroAtracaoDetail.as_view(), name='roteiro-atracao-detail'),

    path('avaliacoes/', views.AvaliacaoList.as_view(), name='avaliacao-list'),
    path('avaliacoes/<uuid:id>/', views.AvaliacaoDetail.as_view(), name='avaliacao-detail'),

    path('ofertas/', views.OfertaList.as_view(), name='oferta-list'),
    path('ofertas/<uuid:id>/', views.OfertaDetail.as_view(), name='oferta-detail'),

    path('denuncias/', views.DenunciaList.as_view(), name='denuncia-list'),
    path('denuncias/<uuid:id>/', views.DenunciaDetail.as_view(), name='denuncia-detail'),

    path('imagens/', views.ImagemList.as_view(), name='imagem-list'), # Add URL for Imagem list
    path('imagens/<uuid:id>/', views.ImagemDetail.as_view(), name='imagem-detail'), # Add URL for Imagem detail
]