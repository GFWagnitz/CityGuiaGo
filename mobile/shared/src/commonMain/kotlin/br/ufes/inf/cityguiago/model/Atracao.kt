package br.ufes.inf.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Atracao(
    val id: String,
    val nome: String,
    val descricao: String,
    val categoria: Categoria, // Use the Categoria data class
    val horario_funcionamento: String?,
    val preco_medio: Double?,
    val endereco_logradouro: String?,
    val endereco_numero: String?,
    val endereco_complemento: String?,
    val endereco_bairro: String?,
    val endereco_cidade: String?,
    val endereco_estado: String?,
    val endereco_cep: String?,
    val endereco_coordenadas: String?,
    val imagens: List<Imagem>
)