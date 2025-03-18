package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Offer(
    val id: String,
    val atracao: Attraction,
    val titulo: String,
    val descricao: String,
    val createdAt: String,
    val preco: Double,
    val dataFim: String,
    val public: Boolean
)