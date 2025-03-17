package com.pi.cityguiago.network

import com.pi.cityguiago.model.AuthResponse
import com.pi.cityguiago.model.SignupRequest
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

class ApiClient(engine: HttpClientEngine) {

    var authToken: String? = null

    val client = HttpClient(engine) {
        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            requestTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                prettyPrint = true
                namingStrategy = JsonNamingStrategy.SnakeCase
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    authToken?.let { BearerTokens(it, it) }
                }
                refreshTokens {
                    authToken?.let { BearerTokens(it, it) }
                }
            }
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
    }

    suspend inline fun <reified T> get(url: String, headers: Map<String, String> = emptyMap()): T {
        val response = client.get(url) {
            authToken?.let { header(HttpHeaders.Authorization, "Token $it")

                println("TOKEN ${HttpHeaders.Authorization}: Token $it")
            }
            headers.forEach { (key, value) -> header(key, value) }
        }

        val responseBody = response.body<T>()
        println("API Response: $responseBody")

        return responseBody
    }

    suspend inline fun <reified T> post(url: String, body: Any, headers: Map<String, String> = emptyMap()): T {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            authToken?.let { header(HttpHeaders.Authorization, "Token $it") }
            headers.forEach { (key, value) -> header(key, value) }
            setBody(body)
        }.body<T>()

        println("API Response: $response")
        return response
    }

    suspend inline fun <reified T> put(url: String, body: Any, headers: Map<String, String> = emptyMap()): T {
        val response = client.put(url) {
            contentType(ContentType.Application.Json)
            headers.forEach { (key, value) -> header(key, value) }
            setBody(body)
        }.body<T>()

        println("API Response: $response")
        return response
    }

    suspend fun delete(url: String, headers: Map<String, String> = emptyMap()): HttpResponse {
        return client.delete(url) {
            headers.forEach { (key, value) -> header(key, value) }
        }
    }
}