package com.pi.cityguiago

sealed class ComponentState {
    data object Idle: ComponentState()
    data object Loading: ComponentState()
    class Error(val error: String? = null): ComponentState()
    class Loaded<T>(val data: T): ComponentState()

    inline fun <reified T> extractData(): T? {
        return if (this is Loaded<*> && data is T) {
            data
        } else {
            null
        }
    }
}