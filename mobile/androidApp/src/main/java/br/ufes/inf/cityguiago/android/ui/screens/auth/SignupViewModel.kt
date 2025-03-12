package br.ufes.inf.cityguiago.android.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufes.inf.cityguiago.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _events = Channel<SignupEvent>()
    val events = _events.receiveAsFlow()
    
    fun signup(nome: String, email: String, password: String) {
        viewModelScope.launch {
            _events.send(SignupEvent.Loading)
            
            val result = authRepository.signup(nome, email, password)
            
            result.fold(
                onSuccess = { _events.send(SignupEvent.Success) },
                onFailure = { _events.send(SignupEvent.Error(it.message ?: "Signup failed")) }
            )
        }
    }
}

sealed class SignupEvent {
    object Loading : SignupEvent()
    object Success : SignupEvent()
    data class Error(val message: String) : SignupEvent()
} 