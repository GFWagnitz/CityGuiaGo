package br.ufes.inf.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Categoria(
    val id: String,
    val descricao: String
)