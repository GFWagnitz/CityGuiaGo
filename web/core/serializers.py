from rest_framework import serializers
from .models import User, Categorias, Atracoes, Roteiros, Avaliacoes, Ofertas, Denuncias, Imagens, RoteiroAtracao
from django.contrib.auth.password_validation import validate_password
from django.contrib.auth import authenticate

class ImagensSerializer(serializers.ModelSerializer):
    image_url = serializers.SerializerMethodField()
    
    class Meta:
        model = Imagens
        fields = ['id', 'caminho', 'image_url', 'atracao', 'user', 'created_at']
        read_only_fields = ['id', 'created_at', 'image_url']
    
    def get_image_url(self, obj):
        request = self.context.get('request')
        if request and obj.caminho:
            return request.build_absolute_uri(obj.caminho)
        return obj.caminho

class UserSerializer(serializers.ModelSerializer):
    imagens = ImagensSerializer(many=True, read_only=True) # Nested serializer, read-only
    password = serializers.CharField(write_only=True, required=True, validators=[validate_password])

    class Meta:
        model = User
        fields = ['id', 'username', 'email', 'nome', 'password', 'created_at', 'avatar', 'imagens']
        read_only_fields = ['id', 'created_at']
        
        
    def create(self, validated_data):
        user = User.objects.create_user(
            username=validated_data['username'],
            email=validated_data.get('email', ''),
            password=validated_data['password']
        )
        
        # Add any optional fields if provided
        if 'nome' in validated_data:
            user.nome = validated_data['nome']
        if 'avatar' in validated_data:
            user.avatar = validated_data['avatar']
            
        user.save()
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

class RoteiroAtracaoSerializer(serializers.ModelSerializer):
    atracao = AtracoesSerializer(read_only=True)
    atracao_id = serializers.PrimaryKeyRelatedField(
        queryset=Atracoes.objects.all(), 
        source='atracao',
        write_only=True
    )
    
    class Meta:
        model = RoteiroAtracao
        fields = ['id', 'atracao', 'atracao_id', 'dia', 'ordem', 'created_at']
        read_only_fields = ['id', 'created_at']

class RoteirosSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)  # Show user details
    categoria = CategoriasSerializer(read_only=True) # Show category details
    atracoes_roteiro = RoteiroAtracaoSerializer(source='roteiroatracao_set', many=True, read_only=True)
    
    class Meta:
        model = Roteiros
        fields = ['id', 'titulo', 'descricao', 'user', 'categoria', 'public', 
                 'created_at', 'duracao', 'atracoes_roteiro']
        read_only_fields = ['id', 'created_at']

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

class LoginSerializer(serializers.Serializer):
    username = serializers.CharField(required=True)
    password = serializers.CharField(required=True, write_only=True)
    
    def validate(self, attrs):
        username = attrs.get('username')
        password = attrs.get('password')
        
        if username and password:
            user = authenticate(username=username, password=password)
            if not user:
                raise serializers.ValidationError('Unable to log in with provided credentials.')
            if not user.is_active:
                raise serializers.ValidationError('User account is disabled.')
        else:
            raise serializers.ValidationError('Must include "username" and "password".')
            
        attrs['user'] = user
        return attrs