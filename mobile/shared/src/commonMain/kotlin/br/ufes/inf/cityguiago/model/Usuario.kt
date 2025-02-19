package br.ufes.inf.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String,
    val nome: String,
    val email: String
)