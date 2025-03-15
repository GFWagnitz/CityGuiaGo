package com.pi.cityguiago.module.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.cityguiago.ComponentState
import com.pi.cityguiago.model.Attraction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeService: HomeService
) : ViewModel() {
    private val _state = MutableStateFlow<ComponentState>(ComponentState.Idle)
    val state = _state.asStateFlow()

    private val _effects: Channel<HomeEffect> = Channel()
    val effects = _effects.receiveAsFlow()

    init {
        loadData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadData -> loadData()
            is HomeEvent.OpenAttractionView -> openAttractionView()
            is HomeEvent.OpenExploreView -> openExploreView(event.attractions)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = ComponentState.Loading

            val result = homeService.getAttractions()

            result.fold(
                onSuccess = { _state.value = ComponentState.Loaded(produceHomeState(it)) },
                onFailure = {
                    _effects.trySend(HomeEffect.ShowErrorMessage("Request Error"))
                    ComponentState.Error("Request Error")
                }
            )
        }
    }

    private fun produceHomeState(attractions: List<Attraction>): HomeState {
        return HomeState(
            attractions = attractions,
            firstAttraction = attractions.getOrNull(0),
            secondAttraction = attractions.getOrNull(1),
            thirdAttraction = attractions.getOrNull(2)
        )
    }

    private fun openAttractionView() {
        _effects.trySend(HomeEffect.OpenAttractionView)
    }

    private fun openExploreView(attractions: List<Attraction>) {
        _effects.trySend(HomeEffect.OpenExploreView(attractions))
    }
}

sealed class HomeEffect {
    data class ShowErrorMessage(val errorMessage: String?) : HomeEffect()
    object OpenAttractionView : HomeEffect()
    data class OpenExploreView(val attractions: List<Attraction>) : HomeEffect()
}

sealed class HomeEvent {
    object LoadData : HomeEvent()
    object OpenAttractionView : HomeEvent()
    data class OpenExploreView(val attractions: List<Attraction>) : HomeEvent()
}