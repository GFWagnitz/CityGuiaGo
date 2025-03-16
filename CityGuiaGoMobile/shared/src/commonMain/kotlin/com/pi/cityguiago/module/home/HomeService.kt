package com.pi.cityguiago.module.home

import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.model.Category
import com.pi.cityguiago.model.Image
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.network.ApiClient

class HomeService(private val apiClient: ApiClient) {
    suspend fun getAttractions(): Result<List<Attraction>> {
        return try {
            val result: List<Attraction> = apiClient.get("https://cityguiago.com/api/atracoes/")
            Result.success(result)
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

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