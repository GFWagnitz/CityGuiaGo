from django.db import models
from django.utils import timezone
from django.contrib.auth.models import AbstractUser
import uuid

class User(AbstractUser):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    nome = models.CharField(max_length=255, blank=True, null=True)
    avatar = models.CharField(max_length=255, blank=True, null=True)
    created_at = models.DateTimeField(default=timezone.now)
    
    # Mapping fields from Usuarios to User fields
    # nome will be used for display, but we'll keep username for login
    # email remains the same
    # password is handled by AbstractUser
    
    def __str__(self):
        return self.nome if self.nome else self.username
    
    class Meta:
        verbose_name = "Usuário"
        verbose_name_plural = "Usuários"

class Categorias(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    categoria_mae = models.ForeignKey('self', on_delete=models.SET_NULL, null=True, blank=True, related_name='subcategorias') # self-referential relationship.  Added related name.  Use SET_NULL
    created_at = models.DateTimeField(default=timezone.now)
    descricao = models.CharField(max_length=255)

    def __str__(self):
        return self.descricao

    class Meta:
        verbose_name = "Categoria"
        verbose_name_plural = "Categorias"

class Atracoes(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    nome = models.CharField(max_length=255)
    descricao = models.CharField(max_length=255)
    categoria = models.ForeignKey(Categorias, on_delete=models.CASCADE) # Use ForeignKey.  CASCADE is a good default.
    created_at = models.DateTimeField(default=timezone.now)
    horario_funcionamento = models.CharField(max_length=255, blank=True, null=True) # Added blank=True, null=True
    preco_medio = models.FloatField(blank=True, null=True) # Added blank=True, null=True
    endereco_logradouro = models.CharField(max_length=255, blank=True, null=True)  # Added blank=True, null=True
    endereco_numero = models.CharField(max_length=255, blank=True, null=True) # Added blank=True, null=True
    endereco_complemento = models.CharField(max_length=255, blank=True, null=True)  # Added blank=True, null=True
    endereco_bairro = models.CharField(max_length=255, blank=True, null=True) # Added blank=True, null=True
    endereco_cidade = models.CharField(max_length=255, blank=True, null=True)  # Added blank=True, null=True
    endereco_estado = models.CharField(max_length=255, blank=True, null=True)  # Added blank=True, null=True
    endereco_cep = models.CharField(max_length=255, blank=True, null=True)  # Added blank=True, null=True
    endereco_coordenadas = models.CharField(max_length=255, blank=True, null=True) # Added blank=True, null=True

    def __str__(self):
        return self.nome

    class Meta:
        verbose_name = "Atração"
        verbose_name_plural = "Atrações"


class Roteiros(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    titulo = models.CharField(max_length=255)
    descricao = models.CharField(max_length=255)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    categoria = models.ForeignKey(Categorias, on_delete=models.SET_NULL, null=True, blank=True)
    public = models.BooleanField(default=False)  # Added default value
    created_at = models.DateTimeField(default=timezone.now)
    duracao = models.IntegerField(blank=True, null=True)  # Renamed from duracao_estimada
    atracoes = models.ManyToManyField(Atracoes, through='RoteiroAtracao', related_name='roteiros')

    def __str__(self):
        return self.titulo
    
    class Meta:
        verbose_name = "Roteiro"
        verbose_name_plural = "Roteiros"


class RoteiroAtracao(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    roteiro = models.ForeignKey(Roteiros, on_delete=models.CASCADE)
    atracao = models.ForeignKey(Atracoes, on_delete=models.CASCADE)
    dia = models.PositiveIntegerField(default=1)  # Starting from 1, not 0
    ordem = models.PositiveIntegerField(default=1)  # Order within the entire roteiro
    created_at = models.DateTimeField(default=timezone.now)

    def save(self, *args, **kwargs):
        # If this is a new entry or the order has changed
        if not self.pk or self._state.adding:
            # Get all roteiro_atracoes with the same roteiro and greater or equal ordem
            subsequent_entries = RoteiroAtracao.objects.filter(
                roteiro=self.roteiro, 
                ordem__gte=self.ordem
            ).exclude(pk=self.pk)
            
            # Increment ordem for all subsequent entries
            for entry in subsequent_entries:
                entry.ordem += 1
                entry.save()
        
        super().save(*args, **kwargs)
    
    def delete(self, *args, **kwargs):
        ordem = self.ordem
        roteiro = self.roteiro
        
        # Delete this entry
        super().delete(*args, **kwargs)
        
        # Update the ordem of all subsequent entries
        subsequent_entries = RoteiroAtracao.objects.filter(
            roteiro=roteiro, 
            ordem__gt=ordem
        )
        
        for entry in subsequent_entries:
            entry.ordem -= 1
            entry.save()
    
    def __str__(self):
        return f"{self.roteiro.titulo} - {self.atracao.nome} (Dia {self.dia}, Ordem {self.ordem})"
    
    class Meta:
        verbose_name = "Roteiro-Atração"
        verbose_name_plural = "Roteiro-Atrações"
        ordering = ['roteiro', 'ordem']
        unique_together = [['roteiro', 'ordem']]

class Avaliacoes(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    atracao = models.ForeignKey(Atracoes, on_delete=models.CASCADE, null=True, blank=True) # Use ForeignKey.  CASCADE is a good default.
    roteiro = models.ForeignKey(Roteiros, on_delete=models.CASCADE, null=True, blank=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    created_at = models.DateTimeField(default=timezone.now)
    nota = models.IntegerField()
    comentario = models.CharField(max_length=255, blank=True, null=True) # Added blank=True, null=True

    def __str__(self):
        return f"Avaliação de {self.user.nome} para {self.atracao.nome if self.atracao else self.roteiro.titulo}"

    class Meta:
        verbose_name = "Avaliação"
        verbose_name_plural = "Avaliações"



class Ofertas(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    titulo = models.CharField(max_length=255)
    descricao = models.CharField(max_length=255)
    atracao = models.ForeignKey(Atracoes, on_delete=models.CASCADE)
    created_at = models.DateTimeField(default=timezone.now)
    preco = models.FloatField()
    data_fim = models.DateTimeField(blank=True, null=True) # Added blank=True, null=True for optional end date
    public = models.BooleanField(default=True) # Added default

    def __str__(self):
        return self.titulo
    
    class Meta:
        verbose_name = "Oferta"
        verbose_name_plural = "Ofertas"



class Denuncias(models.Model):
    STATUS_CHOICES = (
        ('pendente', 'Pendente'),
        ('em_analise', 'Em Análise'),
        ('concluida', 'Concluída'),
        ('arquivada', 'Arquivada'),
    )

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    descricao = models.CharField(max_length=255)
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default='pendente') # Use choices for enums.  Added default.
    atracao = models.ForeignKey(Atracoes, on_delete=models.CASCADE, null=True, blank=True)
    roteiro = models.ForeignKey(Roteiros, on_delete=models.CASCADE, null=True, blank=True)
    oferta = models.ForeignKey(Ofertas, on_delete=models.CASCADE, null=True, blank=True)
    created_at = models.DateTimeField(default=timezone.now)
    data_conclusao = models.DateTimeField(null=True, blank=True)
    parecer_moderador = models.CharField(max_length=255, blank=True, null=True)  # Added blank=True, null=True

    def __str__(self):
        return f"Denúncia de {self.user.nome}"

    class Meta:
        verbose_name = "Denúncia"
        verbose_name_plural = "Denúncias"

class Imagens(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    caminho = models.CharField(max_length=255)  # Consider using ImageField or FileField
    atracao = models.ForeignKey(Atracoes, on_delete=models.CASCADE, null=True, blank=True, related_name='imagens') # Use ForeignKey.  CASCADE.
    user = models.ForeignKey(User, on_delete=models.CASCADE, null=True, blank=True, related_name='imagens')
    created_at = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return self.caminho
    
    class Meta:
        verbose_name = "Imagem"
        verbose_name_plural = "Imagens"

