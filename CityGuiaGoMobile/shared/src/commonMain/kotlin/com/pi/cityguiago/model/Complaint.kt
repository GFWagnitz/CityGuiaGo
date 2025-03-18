package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Complaint(
    val id: String,
    val title: String,
    val type: String
)