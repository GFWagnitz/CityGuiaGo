package br.ufes.inf.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Avaliacao(
    val id: String,
    val atracao: String? = null,
    val roteiro: String? = null,
    val user: String,
    val nota: Int,
    val comentario: String? = null
)

@Serializable
data class AvaliacaoRequest(
    val atracao: String? = null,
    val roteiro: String? = null,
    val nota: Int,
    val comentario: String? = null
) 