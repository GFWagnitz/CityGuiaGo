package com.pi.cityguiago.module.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.cityguiago.ComponentState
import com.pi.cityguiago.model.Attraction
import com.pi.cityguiago.model.Category
import com.pi.cityguiago.model.Itinerary
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
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
            is HomeEvent.OnAttractionClick -> openAttractionView(event.attractionId)
            is HomeEvent.OnItineraryClick -> openItinerariesView()
            is HomeEvent.OnSeachBarClick -> openExploreView()
            is HomeEvent.OnFavoriteButtonClick -> openItinerariesView()
            is HomeEvent.OnItineraryListButtonClick -> openItinerariesView()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = ComponentState.Loading

            try {
                coroutineScope {
                    val attractionsDeferred = async { homeService.getAttractions() }
                    val itinerariesDeferred = async { homeService.getItineraries() }

                    val attractionsResult = attractionsDeferred.await()
                    val itinerariesResult = itinerariesDeferred.await()

                    val attractions = attractionsResult.getOrElse { emptyList() }
                    val itineraries = itinerariesResult.getOrElse { emptyList() }

                    _state.value = ComponentState.Loaded(
                        produceHomeState(attractions, itineraries)
                    )
                }
            } catch (e: Exception) {
                _effects.trySend(HomeEffect.ShowErrorMessage("Request Error"))
                _state.value = ComponentState.Error("Request Error")
            }
        }
    }

    private fun produceHomeState(
        attractions: List<Attraction>,
        itineraries: List<Itinerary>
    ): HomeState {
        val categorizedAttractions = attractions
            .groupBy { it.categoria.descricao }
            .map { (category, attractions) ->
                CategoryAttraction(
                    categoria = category,
                    attractions = attractions
                )
            }

        return HomeState(
            attractions = categorizedAttractions,
            firstAttraction = attractions.getOrNull(0),
            secondAttraction = attractions.getOrNull(1),
            thirdAttraction = attractions.getOrNull(2),
            itineraries = itineraries
        )
    }

    private fun openAttractionView(attractionId: String) {
        _effects.trySend(HomeEffect.OpenAttractionView(attractionId))
    }

    private fun openExploreView() {
        val homeState = _state.value.extractData<HomeState>()
        homeState?.let {
            _effects.trySend(HomeEffect.OpenExploreView(it.attractions))
        }
    }

    private fun openItinerariesView() {
        _effects.trySend(HomeEffect.OpenItinerariesView)
    }
}

sealed class HomeEffect {
    data class ShowErrorMessage(val errorMessage: String?) : HomeEffect()
    data class OpenAttractionView(val attractionId: String) : HomeEffect()
    data class OpenExploreView(val attractions: List<CategoryAttraction>) : HomeEffect()
    object OpenItinerariesView : HomeEffect()
}

sealed class HomeEvent {
    object LoadData : HomeEvent()
    data class OnAttractionClick(val attractionId: String) : HomeEvent()
    object OnItineraryClick : HomeEvent()
    object OnFavoriteButtonClick : HomeEvent()
    object OnItineraryListButtonClick : HomeEvent()
    data class OnSeachBarClick(val attractions: List<CategoryAttraction>) : HomeEvent()
}