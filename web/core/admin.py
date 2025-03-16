from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import User, Categorias, Atracoes, Roteiros, Avaliacoes, Ofertas, Denuncias, Imagens

# Inline models for nested representation in the admin

class ImagensInline(admin.TabularInline):  # Or admin.StackedInline
    model = Imagens
    extra = 1  # Number of empty forms to display


class SubcategoriasInline(admin.TabularInline):
    model = Categorias
    fk_name = 'categoria_mae'  # Specify the foreign key for self-referential relationship
    extra = 1

class AvaliacoesInline(admin.TabularInline):
    model = Avaliacoes
    extra = 1

@admin.register(User)
class UserAdmin(BaseUserAdmin):
    list_display = ('username', 'email', 'nome', 'created_at', 'is_staff')
    search_fields = ('username', 'email', 'nome')
    list_filter = ('created_at', 'is_staff', 'is_superuser')
    inlines = [ImagensInline]
    fieldsets = (
        (None, {'fields': ('username', 'password')}),
        ('Personal info', {'fields': ('nome', 'email', 'avatar')}),
        ('Permissions', {'fields': ('is_active', 'is_staff', 'is_superuser', 'groups', 'user_permissions')}),
        ('Important dates', {'fields': ('created_at', 'last_login', 'date_joined')}),
    )
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('username', 'email', 'nome', 'password1'),
        }),
    )

@admin.register(Categorias)
class CategoriasAdmin(admin.ModelAdmin):
    list_display = ('descricao', 'categoria_mae', 'created_at')
    search_fields = ('descricao',)
    list_filter = ('created_at', 'categoria_mae')
    inlines = [SubcategoriasInline]  # Show subcategories within the parent category


@admin.register(Atracoes)
class AtracoesAdmin(admin.ModelAdmin):
    list_display = (
        'nome',
        'categoria',
        'created_at',
        'preco_medio',
        'endereco_cidade',
        'endereco_estado',
    )
    search_fields = ('nome', 'descricao', 'endereco_cidade', 'endereco_estado')
    list_filter = ('categoria', 'created_at', 'endereco_estado', 'endereco_cidade')
    inlines = [ImagensInline, AvaliacoesInline]  # Display related images inline
    fieldsets = (  # Organize fields into sections
        (None, {
            'fields': ('nome', 'descricao', 'categoria')
        }),
        ('Horário e Preço', {
            'fields': ('horario_funcionamento', 'preco_medio'),
            'classes': ('collapse',),  # Make this section collapsible
        }),
        ('Endereço', {
            'fields': (
                'endereco_logradouro',
                'endereco_numero',
                'endereco_complemento',
                'endereco_bairro',
                'endereco_cidade',
                'endereco_estado',
                'endereco_cep',
                'endereco_coordenadas',
            ),
             'classes': ('collapse',),
        }),

        ('Outras informações', {
            'fields': ('created_at',),
            'classes': ('collapse',),

        })
    )


@admin.register(Roteiros)
class RoteirosAdmin(admin.ModelAdmin):
    list_display = ('titulo', 'user', 'categoria', 'public', 'created_at', 'duracao_estimada')
    search_fields = ('titulo', 'descricao', 'user__nome')  # Search user by name
    list_filter = ('public', 'created_at', 'categoria', 'user')
    inlines = [AvaliacoesInline]

@admin.register(Avaliacoes)
class AvaliacoesAdmin(admin.ModelAdmin):
    list_display = ('user', 'atracao', 'roteiro', 'nota', 'created_at')
    search_fields = ('user__nome', 'atracao__nome', 'roteiro__titulo', 'comentario')  # Search related fields
    list_filter = ('created_at', 'nota', 'user', 'atracao', 'roteiro')


@admin.register(Ofertas)
class OfertasAdmin(admin.ModelAdmin):
    list_display = ('titulo', 'atracao', 'preco', 'data_fim', 'public', 'created_at')
    search_fields = ('titulo', 'descricao', 'atracao__nome')
    list_filter = ('public', 'created_at', 'data_fim', 'atracao')


@admin.register(Denuncias)
class DenunciasAdmin(admin.ModelAdmin):
    list_display = ('user', 'status', 'atracao', 'roteiro', 'oferta', 'created_at', 'data_conclusao')
    search_fields = ('user__nome', 'descricao', 'atracao__nome', 'roteiro__titulo', 'oferta__titulo')
    list_filter = ('status', 'created_at', 'data_conclusao', 'user', 'atracao', 'roteiro', 'oferta')
    readonly_fields = ('created_at', 'data_conclusao')  # Make creation and conclusion dates read-only


@admin.register(Imagens)
class ImagensAdmin(admin.ModelAdmin):
    list_display = ('caminho', 'atracao', 'user', 'created_at')
    search_fields = ('caminho', 'atracao__nome', 'user__nome')
    list_filter = ('created_at', 'atracao', 'user')
    