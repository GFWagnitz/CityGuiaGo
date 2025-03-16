package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Itinerary(
    val id: String,
    val titulo: String,
    val descricao: String,
    val public: Boolean,
    val duracaoEstimada: String? = null,
    val user: User,
    val categoria: String? = null
)