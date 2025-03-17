package com.pi.cityguiago.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val descricao: String,
    val categoriaMae: Category?,
    val subcategorias: List<Category>?,
    val createdAt: String
)