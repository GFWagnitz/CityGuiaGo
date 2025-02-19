package br.ufes.inf.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Imagem(
    val id: String,
    val caminho: String
)