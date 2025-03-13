package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val descricao: String
)