package br.ufes.inf.cityguiago.android.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufes.inf.cityguiago.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _events.send(LoginEvent.Loading)
            
            val result = authRepository.login(email, password)
            
            result.fold(
                onSuccess = { _events.send(LoginEvent.Success) },
                onFailure = { _events.send(LoginEvent.Error(it.message ?: "Login failed")) }
            )
        }
    }
}

sealed class LoginEvent {
    object Loading : LoginEvent()
    object Success : LoginEvent()
    data class Error(val message: String) : LoginEvent()
} 