package com.pi.cityguiago.module.Register

import com.pi.cityguiago.model.AuthResponse
import com.pi.cityguiago.model.SignupRequest
import com.pi.cityguiago.network.ApiClient

class RegisterService(private val apiClient: ApiClient) {
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val result: AuthResponse = apiClient.post(
                "https://cityguiago.com/api/auth/signup/",
                SignupRequest(username, email, password)
            )
            apiClient.authToken = result.token
            Result.success(result)
        } catch (e: Exception) {
            println("Registration failed: ${e.message}")
            Result.failure(e)
        }
    }
}