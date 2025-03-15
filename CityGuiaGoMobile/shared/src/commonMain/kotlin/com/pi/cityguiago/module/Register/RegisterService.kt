package com.pi.cityguiago.module.Register

import com.pi.cityguiago.model.AuthResponse
import com.pi.cityguiago.model.SignupRequest
import com.pi.cityguiago.network.ApiClient

class RegisterService(private val apiClient: ApiClient) {
    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val result: AuthResponse = apiClient.post(
                "https://cityguiago.com/api/auth/register/",
                SignupRequest(name, email, password),

            )
            Result.success(result)
        } catch (e: Exception) {
            println("Registration failed: ${e.message}")
            Result.failure(e)
        }
    }
}