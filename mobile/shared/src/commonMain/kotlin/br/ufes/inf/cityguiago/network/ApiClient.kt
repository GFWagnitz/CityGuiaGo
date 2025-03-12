package br.ufes.inf.cityguiago.network

import br.ufes.inf.cityguiago.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient(engine: HttpClientEngine) {
    private var authToken: String? = null
    
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                prettyPrint = true
            })
        }
        
        install(Auth) {
            bearer {
                loadTokens {
                    authToken?.let { BearerTokens(it, it) }
                }
            }
        }
    }

    private val baseUrl = "https://cityguiago.com/api"

    // Authentication methods
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/auth/login/") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            
            if (response.status.isSuccess()) {
                val authResponse: AuthResponse = response.body()
                authToken = authResponse.token
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signup(nome: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/auth/register/") {
                contentType(ContentType.Application.Json)
                setBody(SignupRequest(nome, email, password))
            }
            
            if (response.status.isSuccess()) {
                val authResponse: AuthResponse = response.body()
                authToken = authResponse.token
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Signup failed: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        authToken = null
    }
    
    // User methods
    suspend fun getCurrentUser(): Result<Usuario> {
        return try {
            val response = client.get("$baseUrl/me/")
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get user: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Attraction methods
    suspend fun getAtracoes(): Result<List<Atracao>> {
        return try {
            val response = client.get("$baseUrl/atracoes/")
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get attractions: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAtracao(id: String): Result<Atracao> {
        return try {
            val response = client.get("$baseUrl/atracoes/$id/")
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get attraction: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Category methods
    suspend fun getCategorias(): Result<List<Categoria>> {
        return try {
            val response = client.get("$baseUrl/categorias/")
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get categories: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Review methods
    suspend fun getAvaliacoes(): Result<List<Avaliacao>> {
        return try {
            val response = client.get("$baseUrl/avaliacoes/")
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get reviews: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAvaliacoesForAtracao(atracaoId: String): Result<List<Avaliacao>> {
        return try {
            val response = client.get("$baseUrl/avaliacoes/?atracao=$atracaoId")
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get reviews: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun postAvaliacao(request: AvaliacaoRequest): Result<Avaliacao> {
        return try {
            val response = client.post("$baseUrl/avaliacoes/") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to post review: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}