package com.pi.cityguiago.module.Register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.cityguiago.ComponentState
import com.pi.cityguiago.module.Login.LoginEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerService: RegisterService
) : ViewModel() {
    private val _state = MutableStateFlow<ComponentState>(ComponentState.Idle)
    val state = _state.asStateFlow()

    private val _effects: Channel<RegisterEffect> = Channel()
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.Register -> register(event.name, event.email, event.password, event.passwordConfirmation)
        }
    }

    fun register(name: String, email: String, password: String, passwordConfirmation: String) {
        if (!checkRegisterData(name, email, password, passwordConfirmation)) {
            _effects.trySend(RegisterEffect.ShowErrorMessage("Missing Data"))
            return
        }

        if (!checkPassword(password, passwordConfirmation)) {
            _effects.trySend(RegisterEffect.ShowErrorMessage("Passwords don't match\""))
            return
        }

        viewModelScope.launch {
            _state.value = ComponentState.Loading

            val result = registerService.register(name, email, password)

            result.fold(
                onSuccess = { _effects.trySend(RegisterEffect.RegisterSuccess) },
                onFailure = {
                    _effects.trySend(RegisterEffect.ShowErrorMessage("Invalid credentials"))
                    ComponentState.Error("Invalid credentials")
                }
            )
        }
    }

    private fun checkRegisterData(name: String, email: String, password: String, passwordConfirmation:String): Boolean {
        return name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConfirmation.isNotEmpty()
    }

    private fun checkPassword(password: String, passwordConfirmation:String): Boolean {
        return password == passwordConfirmation
    }
}

sealed class RegisterEffect {
    data class ShowErrorMessage(val errorMessage: String?) : RegisterEffect()
    object RegisterSuccess : RegisterEffect()
}

sealed class RegisterEvent {
    data class Register(val name: String, val email: String, val password: String, val passwordConfirmation: String) : RegisterEvent()
}