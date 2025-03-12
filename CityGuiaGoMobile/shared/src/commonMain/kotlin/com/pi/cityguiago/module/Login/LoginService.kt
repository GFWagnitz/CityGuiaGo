package com.pi.cityguiago.module.Login

import com.pi.cityguiago.model.AuthResponse
import com.pi.cityguiago.model.LoginRequest
import com.pi.cityguiago.network.ApiClient

class LoginService(private val apiClient: ApiClient) {
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val result: AuthResponse = apiClient.post(
                "https://cityguiago.com/api/auth/login/",
                LoginRequest(email, password)
            )
            Result.success(result)
        } catch (e: Exception) {
            println("Registration failed: ${e.message}")
            Result.failure(e)
        }
    }
}