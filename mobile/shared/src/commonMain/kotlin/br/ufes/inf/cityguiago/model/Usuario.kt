package br.ufes.inf.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: String,
    val avatar: Imagem?,
    val imagens: List<Imagem>,
)