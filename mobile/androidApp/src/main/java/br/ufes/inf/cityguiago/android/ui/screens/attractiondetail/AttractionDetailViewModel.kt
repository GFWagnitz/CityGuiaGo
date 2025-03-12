package br.ufes.inf.cityguiago.android.ui.screens.attractiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufes.inf.cityguiago.model.Atracao
import br.ufes.inf.cityguiago.model.Avaliacao
import br.ufes.inf.cityguiago.repository.AttractionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AttractionDetailViewModel(
    private val attractionRepository: AttractionRepository
) : ViewModel() {
    
    private val _attraction = MutableStateFlow<Atracao?>(null)
    val attraction: StateFlow<Atracao?> = _attraction.asStateFlow()
    
    private val _reviews = MutableStateFlow<List<Avaliacao>>(emptyList())
    val reviews: StateFlow<List<Avaliacao>> = _reviews.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _events = Channel<AttractionDetailEvent>()
    val events = _events.receiveAsFlow()
    
    fun loadAttractionDetails(attractionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Load attraction details
                attractionRepository.fetchAttraction(attractionId)
                _attraction.value = attractionRepository.selectedAttraction.value
                
                // Load reviews for this attraction
                attractionRepository.fetchReviewsForAttraction(attractionId)
                _reviews.value = attractionRepository.reviews.value
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun submitReview(attractionId: String, rating: Int, comment: String?) {
        viewModelScope.launch {
            _events.send(AttractionDetailEvent.Loading)
            
            val result = attractionRepository.submitReview(
                atracaoId = attractionId,
                nota = rating,
                comentario = comment
            )
            
            result.fold(
                onSuccess = {
                    _reviews.value = attractionRepository.reviews.value
                    _events.send(AttractionDetailEvent.ReviewSubmitted)
                },
                onFailure = {
                    _events.send(AttractionDetailEvent.Error(it.message ?: "Failed to submit review"))
                }
            )
        }
    }
}

sealed class AttractionDetailEvent {
    object Loading : AttractionDetailEvent()
    object ReviewSubmitted : AttractionDetailEvent()
    data class Error(val message: String) : AttractionDetailEvent()
} 