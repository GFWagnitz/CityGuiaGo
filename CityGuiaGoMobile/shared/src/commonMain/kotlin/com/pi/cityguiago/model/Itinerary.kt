package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Itinerary(
    val id: String,
    val titulo: String,
    val descricao: String,
    val public: Boolean,
    val duracao: Int? = null,
    val user: User,
    val categoria: String? = null,
    val isFavorite: Boolean = false,
    val attractions: List<ItineraryAttraction> = emptyList()
)

@Serializable
data class ItineraryAttraction(
    val id: String,
    val roteiro: String,
    val atracao: Attraction,
    val dia: Int,
    val ordem: Int
)