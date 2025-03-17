package com.pi.cityguiago.module.Itinerary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.cityguiago.ComponentState
import com.pi.cityguiago.module.home.CategoryAttraction
import com.pi.cityguiago.module.home.HomeEffect
import com.pi.cityguiago.module.home.HomeService
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ItineraryViewModel(
    private val itineraryService: ItineraryService
) : ViewModel() {
    private val _state = MutableStateFlow<ComponentState>(ComponentState.Idle)
    val state = _state.asStateFlow()

    private val _effects: Channel<ItineraryEffect> = Channel()
    val effects = _effects.receiveAsFlow()

    init {
        loadDate()
    }

    private fun loadDate() {
        viewModelScope.launch {
            _state.value = ComponentState.Loading

            try {
                val itinerariesResult = itineraryService.getItineraries()
                val itineraries = itinerariesResult.getOrElse { emptyList() }

                _state.value = ComponentState.Loaded(itineraries)

            } catch (e: Exception) {
                _effects.trySend(ItineraryEffect.ShowErrorMessage("Request Error"))
                _state.value = ComponentState.Error("Request Error")
            }
        }
    }
}

sealed class ItineraryEffect {
    data class ShowErrorMessage(val errorMessage: String?) : ItineraryEffect()
}