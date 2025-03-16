from rest_framework import serializers
from .models import User, Categorias, Atracoes, Roteiros, Avaliacoes, Ofertas, Denuncias, Imagens

class ImagensSerializer(serializers.ModelSerializer):
    class Meta:
        model = Imagens
        fields = ['id', 'caminho', 'atracao', 'user', 'created_at']  # Include all fields
        read_only_fields = ['id', 'created_at']

class UserSerializer(serializers.ModelSerializer):
    imagens = ImagensSerializer(many=True, read_only=True) # Nested serializer, read-only

    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'nome', 'created_at', 'avatar', 'imagens']  # Include all fields
        read_only_fields = ['id', 'created_at']
        extra_kwargs = {'password': {'write_only': True}}
        
    def create(self, validated_data):
        user = User.objects.create_user(**validated_data)
        return user

class CategoriasSerializer(serializers.ModelSerializer):
    subcategorias = serializers.PrimaryKeyRelatedField(many=True, read_only=True)

    class Meta:
        model = Categorias
        fields = ['id', 'categoria_mae', 'created_at', 'descricao', 'subcategorias']
        read_only_fields = ['id', 'created_at']

class AtracoesSerializer(serializers.ModelSerializer):
    categoria = CategoriasSerializer(read_only=True)  # Show category details
    imagens = ImagensSerializer(many=True, read_only=True)  # Nested serializer

    class Meta:
        model = Atracoes
        fields = '__all__'  # Include all fields for simplicity, since we only have GET/LIST
        read_only_fields = [field.name for field in Atracoes._meta.fields]  # Make all fields read-only


class RoteirosSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)  # Show user details
    categoria = CategoriasSerializer(read_only=True) # Show category details
    class Meta:
        model = Roteiros
        fields = '__all__'
        read_only_fields = [field.name for field in Roteiros._meta.fields]

class AvaliacoesSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    atracao = AtracoesSerializer(read_only=True)  # Show attraction details
    roteiro = RoteirosSerializer(read_only=True) #Show roteiro details.
    class Meta:
        model = Avaliacoes
        fields = '__all__'
        read_only_fields = [field.name for field in Avaliacoes._meta.fields]

class OfertasSerializer(serializers.ModelSerializer):
    atracao = AtracoesSerializer(read_only=True)
    class Meta:
        model = Ofertas
        fields = '__all__'
        read_only_fields = [field.name for field in Ofertas._meta.fields]

class DenunciasSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    atracao = AtracoesSerializer(read_only=True)
    roteiro = RoteirosSerializer(read_only=True)
    oferta = OfertasSerializer(read_only=True)
    class Meta:
        model = Denuncias
        fields = '__all__'
        read_only_fields = [field.name for field in Denuncias._meta.fields]