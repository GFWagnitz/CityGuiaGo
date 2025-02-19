package br.ufes.inf.cityguiago.network

import br.ufes.inf.cityguiago.model.Atracao
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient(engine: HttpClientEngine) {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    private val baseUrl = "https://cityguiago.com/api"

    suspend fun getAtracoes(): List<Atracao> {
        return try {
            client.get("$baseUrl/atracoes/").body()
        } catch (e: Exception) {
            // Handle network errors (e.g., no internet, server down)
            println("Error fetching attractions: ${e.message}")
            emptyList() // Return an empty list or throw a custom exception
        }
    }
}