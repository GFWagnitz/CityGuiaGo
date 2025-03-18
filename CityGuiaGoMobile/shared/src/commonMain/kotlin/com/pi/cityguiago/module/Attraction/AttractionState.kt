package com.pi.cityguiago.module.Attraction

import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.model.Offer
import com.pi.cityguiago.model.Review

data class AttractionState(
    val attraction: Attraction,
    val offers: List<Offer> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val itineraries: List<Itinerary> = emptyList(),
    val reviewsCount: Int,
    val rating: Double
)
