package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Attraction(
    val id: String,
    val nome: String,
    val descricao: String,
    val categoria: Category,
    val horarioFuncionamento: String?,
    val precoMedio: Double?,
    val enderecoLogradouro: String?,
    val enderecoNumero: String?,
    val enderecoComplemento: String?,
    val enderecoBairro: String?,
    val enderecoCidade: String?,
    val enderecoEstado: String?,
    val enderecoCep: String?,
    val enderecoCoordenadas: String?,
    val imagens: List<Image>
)