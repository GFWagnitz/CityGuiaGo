package com.pi.cityguiago.module.Attraction

import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.model.Category
import com.pi.cityguiago.model.Image
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.model.Offer
import com.pi.cityguiago.model.Review
import com.pi.cityguiago.network.ApiClient

class AttractionService(private val apiClient: ApiClient) {
    suspend fun getAttraction(id: String): Result<Attraction> {
        return try {
            val result: Attraction = apiClient.get("https://cityguiago.com/api/atracoes/${id}")
            Result.success(result)
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun getOffers(): Result<List<Offer>> {
        return try {
            val result: List<Offer> = apiClient.get("https://cityguiago.com/api/ofertas")
            Result.success(result)
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    suspend fun getReviews(): Result<List<Review>> {
        return try {
            val result: List<Review> = apiClient.get("https://cityguiago.com/api/avaliacoes")
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