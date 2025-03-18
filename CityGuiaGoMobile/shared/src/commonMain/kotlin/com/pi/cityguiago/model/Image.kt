package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: String,
    val caminho: String,
    val imageUrl: String,
)