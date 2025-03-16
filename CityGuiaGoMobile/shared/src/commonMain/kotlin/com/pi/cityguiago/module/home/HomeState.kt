package com.pi.cityguiago.module.home

import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.model.Category
import com.pi.cityguiago.model.Image
import com.pi.cityguiago.model.Itinerary
import kotlinx.serialization.Serializable

data class HomeState(
    val attractions: List<CategoryAttraction> = emptyList(),
    val firstAttraction: Attraction? = null,
    val secondAttraction: Attraction? = null,
    val thirdAttraction: Attraction? = null,
    val itineraries: List<Itinerary> = emptyList()
)

data class CategoryAttraction(
    val categoria: String,
    val attractions: List<Attraction>
)