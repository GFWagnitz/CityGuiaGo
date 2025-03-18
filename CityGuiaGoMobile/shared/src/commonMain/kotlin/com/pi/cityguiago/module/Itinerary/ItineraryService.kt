package com.pi.cityguiago.module.Itinerary

import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.model.ItineraryAttraction
import com.pi.cityguiago.network.ApiClient
import com.pi.cityguiago.network.PrefCacheManager

class ItineraryService(
    private val apiClient: ApiClient,
    private val prefManager: PrefCacheManager
) {
    suspend fun getPublicItineraries(): Result<List<Itinerary>> {
        return try {
            val result: List<Itinerary> = apiClient.get("https://cityguiago.com/api/roteiros/")
            Result.success(result)
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
    
    suspend fun getFavoriteItineraries(): Result<List<Itinerary>> {
        return try {
            // This will be implemented with local storage later
            // For now, we'll get all itineraries and filter the favorites
            val favorites = prefManager.getFavoriteItineraryIds() ?: emptySet()
            
            val allItineraries: List<Itinerary> = apiClient.get("https://cityguiago.com/api/roteiros/")
            val favoriteItineraries = allItineraries.filter { it.id in favorites }.map {
                it.copy(isFavorite = true)
            }
            
            Result.success(favoriteItineraries)
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
    
    suspend fun getItineraryDetails(itineraryId: String): Result<Pair<Itinerary, List<ItineraryAttraction>>> {
        return try {
            val itinerary: Itinerary = apiClient.get("https://cityguiago.com/api/roteiros/$itineraryId/")
            val attractions: List<ItineraryAttraction> = apiClient.get("https://cityguiago.com/api/roteiros/$itineraryId/atracoes/")
            
            // Check if this is a favorite
            val favorites = prefManager.getFavoriteItineraryIds() ?: emptySet()
            val updatedItinerary = itinerary.copy(isFavorite = itinerary.id in favorites)
            
            Result.success(Pair(updatedItinerary, attractions))
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
    
    suspend fun toggleFavorite(itineraryId: String, isFavorite: Boolean): Result<Boolean> {
        return try {
            val favorites = prefManager.getFavoriteItineraryIds()?.toMutableSet() ?: mutableSetOf()
            
            if (isFavorite) {
                favorites.add(itineraryId)
            } else {
                favorites.remove(itineraryId)
            }
            
            prefManager.saveFavoriteItineraryIds(favorites)
            Result.success(isFavorite)
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
}