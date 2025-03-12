package br.ufes.inf.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class SignupRequest(
    val nome: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: Usuario
) 