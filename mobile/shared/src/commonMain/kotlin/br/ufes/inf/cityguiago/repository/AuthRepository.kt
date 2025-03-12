package br.ufes.inf.cityguiago.repository

import br.ufes.inf.cityguiago.model.AuthResponse
import br.ufes.inf.cityguiago.model.Usuario
import br.ufes.inf.cityguiago.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val apiClient: ApiClient) {
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        val result = apiClient.login(email, password)
        
        result.onSuccess {
            _currentUser.value = it.user
            _isLoggedIn.value = true
        }
        
        return result
    }
    
    suspend fun signup(nome: String, email: String, password: String): Result<AuthResponse> {
        val result = apiClient.signup(nome, email, password)
        
        result.onSuccess {
            _currentUser.value = it.user
            _isLoggedIn.value = true
        }
        
        return result
    }
    
    fun logout() {
        apiClient.logout()
        _currentUser.value = null
        _isLoggedIn.value = false
    }
    
    suspend fun refreshUserProfile() {
        if (_isLoggedIn.value) {
            apiClient.getCurrentUser().onSuccess {
                _currentUser.value = it
            }
        }
    }
} 