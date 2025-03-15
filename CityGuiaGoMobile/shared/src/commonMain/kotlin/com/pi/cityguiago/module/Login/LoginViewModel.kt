package com.pi.cityguiago.module.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.cityguiago.ComponentState
import com.pi.cityguiago.model.AuthResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginService: LoginService
) : ViewModel() {
    private val _state = MutableStateFlow<ComponentState>(ComponentState.Idle)
    val state = _state.asStateFlow()

    private val _effects: Channel<LoginEffect> = Channel()
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(event.email, event.password)
        }
    }

    fun login(email: String, password: String) {
        if (!checkLoginData(email, password)) {
            _effects.trySend(LoginEffect.ShowErrorMessage("Missing Email or Password"))
            return
        }

        viewModelScope.launch {
            _state.value = ComponentState.Loading

            val result = loginService.login(email, password)

            result.fold(
                onSuccess = { _effects.trySend(LoginEffect.LoginSuccess(it)) },
                onFailure = {
                    _effects.trySend(LoginEffect.ShowErrorMessage("Invalid credentials"))
                    ComponentState.Error("Invalid credentials")
                }
            )
        }
    }

    private fun checkLoginData(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}

sealed class LoginEffect {
    data class ShowErrorMessage(val errorMessage: String?) : LoginEffect()
    data class LoginSuccess(val user: AuthResponse) : LoginEffect()
}

sealed class LoginEvent {
    data class Login(val email: String, val password: String) : LoginEvent()
}