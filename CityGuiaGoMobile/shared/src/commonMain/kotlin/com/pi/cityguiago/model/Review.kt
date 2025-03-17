package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String,
    val user: User,
    val atracao: Attraction,
    val createdAt: String,
    val nota: Int,
    val comentario: String?
)