package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: String,
    val avatar: Image?,
    val imagens: List<Image>,
)