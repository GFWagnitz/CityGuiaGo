package com.pi.cityguiago.module.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.cityguiago.ComponentState
import com.pi.cityguiago.extension.fixUrl
import com.pi.cityguiago.model.Complaint
import com.pi.cityguiago.module.Attraction.AttractionService
import com.pi.cityguiago.module.Attraction.AttractionState
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AttractionViewModel(
    private val attractionService: AttractionService
) : ViewModel() {
    private val _state = MutableStateFlow<ComponentState>(ComponentState.Idle)
    val state = _state.asStateFlow()

    private val _effects: Channel<AttractionEffect> = Channel()
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: AttractionEvent) {
        when (event) {
            is AttractionEvent.LoadData -> loadAttraction(event.attractionId)
            is AttractionEvent.OnAttractionComplaintClick -> handleAttractionComplaint()
            is AttractionEvent.OnOfferComplaintClick -> handleOfferComplaint(event.offerId, event.offerName)
            is AttractionEvent.OnReviewComplaintClick -> handleReviewComplaint(event.reviewId, event.reviewName)
            is AttractionEvent.AddToItinerary -> addToItinerary(event.itineraryId)
            is AttractionEvent.AddReview -> addReview(event.rating, event.commentary)
            else -> {}
        }
    }

    private fun loadAttraction(attractionId: String) {
        viewModelScope.launch {
            _state.value = ComponentState.Loading

            try {
                coroutineScope {
                    val attractionDeferred = async { attractionService.getAttraction(attractionId) }
                    val offersDeferred = async { attractionService.getOffers() }
                    val reviewsDeferred = async { attractionService.getReviews() }
                    val itinerariesDeferred = async { attractionService.getItineraries() }

                    val attractionResult = attractionDeferred.await()
                    val offersResult = offersDeferred.await()
                    val reviewsResult = reviewsDeferred.await()
                    val itinerariesResult = itinerariesDeferred.await()

                    attractionResult.fold(
                        onSuccess = { attraction ->
                            val offers = offersResult.getOrElse { emptyList() }
                                .filter { it.atracao.id == attractionId }
                            val reviews = reviewsResult.getOrElse { emptyList() }
                                .filter { it.atracao.id == attractionId }
                            val itineraries = itinerariesResult.getOrElse { emptyList() }
                            val reviewsCount = reviews.size
                            val ratings = reviews.map { it.nota }
                            val rating = if (ratings.isNotEmpty()) ratings.average() else 0.0
                            val fixedAttraction = attraction.copy(
                                imagens = attraction.imagens.map { image ->
                                    image.copy(
                                        imageUrl = fixUrl(image.imageUrl)
                                    )
                                }
                            )

                            _state.value = ComponentState.Loaded(
                                AttractionState(
                                    attraction = fixedAttraction,
                                    offers = offers,
                                    reviews = reviews,
                                    itineraries = itineraries,
                                    reviewsCount = reviewsCount,
                                    rating = rating
                                )
                            )
                        },
                        onFailure = {
                            _effects.trySend(AttractionEffect.ShowErrorMessage("Failed to load attraction"))
                            _state.value = ComponentState.Error("Failed to load attraction")
                        }
                    )
                }
            } catch (e: Exception) {
                _effects.trySend(AttractionEffect.ShowErrorMessage("Failed to load data"))
                _state.value = ComponentState.Error("Failed to load data")
            }
        }
    }

    private fun handleAttractionComplaint() {

        val state = _state.value.extractData<AttractionState>()
        state?.let {
            val complaint = Complaint(it.attraction.id, it.attraction.nome, "attraction")
            _effects.trySend(AttractionEffect.OpenComplaintView(complaint))
        }
    }

    private fun handleOfferComplaint(offerId: String, offerName: String) {
        val complaint = Complaint(offerId, offerName, "offer")
        _effects.trySend(AttractionEffect.OpenComplaintView(complaint))
    }

    private fun handleReviewComplaint(reviewId: String, reviewName: String) {
        val complaint = Complaint(reviewId, reviewName, "review")
        _effects.trySend(AttractionEffect.OpenComplaintView(complaint))
    }

    private fun addToItinerary(itineraryId: String) {
        // Handle adding to itinerary
    }

    private fun addReview(rating: Double, commentary: String) {
        // Handle adding review
    }
}

sealed class AttractionEffect {
    data class ShowErrorMessage(val errorMessage: String?) : AttractionEffect()
    data class OpenComplaintView(val complaint: Complaint) : AttractionEffect()
}

sealed class AttractionEvent {
    data class LoadData(val attractionId: String) : AttractionEvent()
    object OnAttractionComplaintClick : AttractionEvent()
    data class OnOfferComplaintClick(val offerId: String, val offerName: String) : AttractionEvent()
    data class OnReviewComplaintClick(val reviewId: String, val reviewName: String) : AttractionEvent()
    data class AddToItinerary(val itineraryId: String) : AttractionEvent()
    data class AddReview(val rating: Double, val commentary: String) : AttractionEvent()
}