package br.ufes.inf.cityguiago.android.ui.screens.attractions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufes.inf.cityguiago.model.Atracao
import br.ufes.inf.cityguiago.repository.AttractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AttractionsViewModel(
    private val attractionRepository: AttractionRepository
) : ViewModel() {
    
    private val _attractions = MutableStateFlow<List<Atracao>>(emptyList())
    val attractions: StateFlow<List<Atracao>> = _attractions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadAttractions() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                attractionRepository.fetchAttractions()
                _attractions.value = attractionRepository.attractions.value
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
} 