package com.pi.cityguiago.module.home

import androidx.lifecycle.ViewModel
import com.pi.cityguiago.ComponentState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class ExploreViewModel() : ViewModel() {
    private val _state = MutableStateFlow<ComponentState>(ComponentState.Idle)
    val state = _state.asStateFlow()

    private val _effects: Channel<ExploreEffect> = Channel()
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: ExploreEvent) {
        when (event) {
            is ExploreEvent.OnAttractionClick -> openAttractionView(event.attractionId)
        }
    }

    private fun openAttractionView(attractionId: String) {
        _effects.trySend(ExploreEffect.OpenAttractionView(attractionId))
    }
}

sealed class ExploreEffect {
    data class OpenAttractionView(val attractionId: String) : ExploreEffect()
}

sealed class ExploreEvent {
    data class OnAttractionClick(val attractionId: String) : ExploreEvent()
}