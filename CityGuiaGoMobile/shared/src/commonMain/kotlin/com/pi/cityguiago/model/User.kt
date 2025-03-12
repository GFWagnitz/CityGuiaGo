package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val nome: String,
    val email: String
)