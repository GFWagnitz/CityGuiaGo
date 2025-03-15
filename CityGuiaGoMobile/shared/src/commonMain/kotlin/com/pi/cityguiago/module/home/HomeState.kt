package com.pi.cityguiago.module.home

import com.pi.cityguiago.model.Attraction

data class HomeState(
    val attractions: List<Attraction> = emptyList(),
    val firstAttraction: Attraction? = null,
    val secondAttraction: Attraction? = null,
    val thirdAttraction: Attraction? = null
)