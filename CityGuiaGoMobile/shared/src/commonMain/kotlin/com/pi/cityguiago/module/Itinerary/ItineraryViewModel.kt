package com.pi.cityguiago.module.Itinerary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.cityguiago.ComponentState
import com.pi.cityguiago.model.Itinerary
import com.pi.cityguiago.model.ItineraryAttraction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ItineraryViewModel(
    private val itineraryService: ItineraryService
) : ViewModel() {
    private val _state = MutableStateFlow<ItineraryState>(ItineraryState.Idle)
    val state = _state.asStateFlow()

    private val _effects: Channel<ItineraryEffect> = Channel()
    val effects = _effects.receiveAsFlow()

    init {
        loadPublicItineraries()
    }

    fun handleAction(action: ItineraryAction) {
        when (action) {
            is ItineraryAction.LoadPublicItineraries -> loadPublicItineraries()
            is ItineraryAction.LoadFavoriteItineraries -> loadFavoriteItineraries()
            is ItineraryAction.LoadItineraryDetails -> loadItineraryDetails(action.itineraryId)
            is ItineraryAction.ToggleFavorite -> toggleFavorite(action.itinerary, action.isFavorite)
        }
    }

    private fun loadPublicItineraries() {
        viewModelScope.launch {
            _state.value = ItineraryState.Loading

            try {
                val itinerariesResult = itineraryService.getPublicItineraries()
                val itineraries = itinerariesResult.getOrElse { emptyList() }

                _state.value = ItineraryState.PublicItinerariesLoaded(itineraries)

            } catch (e: Exception) {
                _effects.trySend(ItineraryEffect.ShowErrorMessage("Erro ao carregar roteiros públicos"))
                _state.value = ItineraryState.Error("Erro ao carregar roteiros públicos")
            }
        }
    }
    
    private fun loadFavoriteItineraries() {
        viewModelScope.launch {
            _state.value = ItineraryState.Loading

            try {
                val itinerariesResult = itineraryService.getFavoriteItineraries()
                val itineraries = itinerariesResult.getOrElse { emptyList() }

                _state.value = ItineraryState.FavoriteItinerariesLoaded(itineraries)

            } catch (e: Exception) {
                _effects.trySend(ItineraryEffect.ShowErrorMessage("Erro ao carregar roteiros favoritos"))
                _state.value = ItineraryState.Error("Erro ao carregar roteiros favoritos")
            }
        }
    }
    
    private fun loadItineraryDetails(itineraryId: String) {
        viewModelScope.launch {
            _state.value = ItineraryState.Loading

            try {
                val detailsResult = itineraryService.getItineraryDetails(itineraryId)
                val (itinerary, attractions) = detailsResult.getOrThrow()
                
                // Group attractions by day
                val attractionsByDay = attractions.groupBy { it.dia }
                
                _state.value = ItineraryState.ItineraryDetailsLoaded(itinerary, attractionsByDay)

            } catch (e: Exception) {
                _effects.trySend(ItineraryEffect.ShowErrorMessage("Erro ao carregar detalhes do roteiro"))
                _state.value = ItineraryState.Error("Erro ao carregar detalhes do roteiro")
            }
        }
    }
    
    private fun toggleFavorite(itinerary: Itinerary, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                val result = itineraryService.toggleFavorite(itinerary.id, isFavorite)
                if (result.isSuccess) {
                    _effects.trySend(
                        if (isFavorite) ItineraryEffect.FavoriteAdded
                        else ItineraryEffect.FavoriteRemoved
                    )
                    
                    // Update the current state to reflect the change
                    when (val currentState = _state.value) {
                        is ItineraryState.PublicItinerariesLoaded -> {
                            val updatedList = currentState.itineraries.map { 
                                if (it.id == itinerary.id) it.copy(isFavorite = isFavorite) else it
                            }
                            _state.value = ItineraryState.PublicItinerariesLoaded(updatedList)
                        }
                        is ItineraryState.FavoriteItinerariesLoaded -> {
                            // If we're removing from favorites, filter it out
                            val updatedList = if (!isFavorite) {
                                currentState.itineraries.filter { it.id != itinerary.id }
                            } else {
                                currentState.itineraries.map { 
                                    if (it.id == itinerary.id) it.copy(isFavorite = true) else it
                                }
                            }
                            _state.value = ItineraryState.FavoriteItinerariesLoaded(updatedList)
                        }
                        is ItineraryState.ItineraryDetailsLoaded -> {
                            _state.value = ItineraryState.ItineraryDetailsLoaded(
                                itinerary.copy(isFavorite = isFavorite),
                                currentState.attractionsByDay
                            )
                        }
                        else -> {} // Other states don't need updates
                    }
                } else {
                    _effects.trySend(ItineraryEffect.ShowErrorMessage("Erro ao atualizar favoritos"))
                }
            } catch (e: Exception) {
                _effects.trySend(ItineraryEffect.ShowErrorMessage("Erro ao atualizar favoritos"))
            }
        }
    }
}

sealed class ItineraryState {
    object Idle : ItineraryState()
    object Loading : ItineraryState()
    data class Error(val message: String) : ItineraryState()
    data class PublicItinerariesLoaded(val itineraries: List<Itinerary>) : ItineraryState()
    data class FavoriteItinerariesLoaded(val itineraries: List<Itinerary>) : ItineraryState()
    data class ItineraryDetailsLoaded(
        val itinerary: Itinerary, 
        val attractionsByDay: Map<Int, List<ItineraryAttraction>>
    ) : ItineraryState()
}

sealed class ItineraryAction {
    object LoadPublicItineraries : ItineraryAction()
    object LoadFavoriteItineraries : ItineraryAction()
    data class LoadItineraryDetails(val itineraryId: String) : ItineraryAction()
    data class ToggleFavorite(val itinerary: Itinerary, val isFavorite: Boolean) : ItineraryAction()
}

sealed class ItineraryEffect {
    data class ShowErrorMessage(val errorMessage: String?) : ItineraryEffect()
    object FavoriteAdded : ItineraryEffect()
    object FavoriteRemoved : ItineraryEffect()
}