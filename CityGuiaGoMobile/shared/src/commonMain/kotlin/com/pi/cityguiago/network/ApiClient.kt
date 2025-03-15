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

class ApiClient(engine: HttpClientEngine) {

    private var authToken: String? = null

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
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    authToken?.let { BearerTokens(it, it) }
                }
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    suspend inline fun <reified T> get(url: String, headers: Map<String, String> = emptyMap()): T {
        return client.get(url) {
            headers.forEach { (key, value) -> header(key, value) }
        }.body<T>()
    }

    suspend inline fun <reified T> post(url: String, body: Any, headers: Map<String, String> = emptyMap()): T {
        return client.post(url) {
            contentType(ContentType.Application.Json)
            headers.forEach { (key, value) -> header(key, value) }
            setBody(body)
        }.body<T>()
    }

    suspend inline fun <reified T> put(url: String, body: Any, headers: Map<String, String> = emptyMap()): T {
        return client.put(url) {
            contentType(ContentType.Application.Json)
            headers.forEach { (key, value) -> header(key, value) }
            setBody(body)
        }.body<T>()
    }

    suspend fun delete(url: String, headers: Map<String, String> = emptyMap()): HttpResponse {
        return client.delete(url) {
            headers.forEach { (key, value) -> header(key, value) }
        }
    }
}