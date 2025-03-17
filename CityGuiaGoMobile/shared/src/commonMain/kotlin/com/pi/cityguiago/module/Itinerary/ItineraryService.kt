package com.pi.cityguiago.module.Itinerary

import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.network.ApiClient

class ItineraryService(private val apiClient: ApiClient) {
    suspend fun getItineraries(): Result<List<Itinerary>> {
        return try {
            val result: List<Itinerary> = apiClient.get("https://cityguiago.com/api/roteiros/")
            Result.success(result)
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
}