package br.ufes.inf.cityguiago.android.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufes.inf.cityguiago.model.Usuario
import br.ufes.inf.cityguiago.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    val currentUser: StateFlow<Usuario?> = authRepository.currentUser
    
    init {
        refreshUserProfile()
    }
    
    private fun refreshUserProfile() {
        viewModelScope.launch {
            authRepository.refreshUserProfile()
        }
    }
    
    fun logout() {
        authRepository.logout()
    }
} 