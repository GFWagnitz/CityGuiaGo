package br.ufes.inf.cityguiago.repository

import br.ufes.inf.cityguiago.model.Atracao
import br.ufes.inf.cityguiago.model.Avaliacao
import br.ufes.inf.cityguiago.model.AvaliacaoRequest
import br.ufes.inf.cityguiago.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AttractionRepository(private val apiClient: ApiClient) {
    private val _attractions = MutableStateFlow<List<Atracao>>(emptyList())
    val attractions: StateFlow<List<Atracao>> = _attractions.asStateFlow()
    
    private val _selectedAttraction = MutableStateFlow<Atracao?>(null)
    val selectedAttraction: StateFlow<Atracao?> = _selectedAttraction.asStateFlow()
    
    private val _reviews = MutableStateFlow<List<Avaliacao>>(emptyList())
    val reviews: StateFlow<List<Avaliacao>> = _reviews.asStateFlow()
    
    suspend fun fetchAttractions() {
        apiClient.getAtracoes().onSuccess {
            _attractions.value = it
        }
    }
    
    suspend fun fetchAttraction(id: String) {
        apiClient.getAtracao(id).onSuccess {
            _selectedAttraction.value = it
        }
    }
    
    suspend fun fetchReviewsForAttraction(atracaoId: String) {
        apiClient.getAvaliacoesForAtracao(atracaoId).onSuccess {
            _reviews.value = it
        }
    }
    
    suspend fun submitReview(atracaoId: String, nota: Int, comentario: String?): Result<Avaliacao> {
        val request = AvaliacaoRequest(
            atracao = atracaoId,
            nota = nota,
            comentario = comentario
        )
        
        val result = apiClient.postAvaliacao(request)
        
        result.onSuccess {
            // Refresh reviews after successful submission
            fetchReviewsForAttraction(atracaoId)
        }
        
        return result
    }
} 