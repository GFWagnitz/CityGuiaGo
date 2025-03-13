package com.pi.cityguiago.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pi.cityguiago.model.AuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PrefCacheManager(
    private val dataStore: DataStore<Preferences>
) {
    private val AUTH_KEY = stringPreferencesKey("auth_response")

    suspend fun saveUser(authResponse: AuthResponse) {
        withContext(Dispatchers.IO) {
            try {
                val json = Json.encodeToString(authResponse)
                dataStore.edit { it[AUTH_KEY] = json }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getUser(): AuthResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val json = dataStore.data
                    .map { it[AUTH_KEY] }
                    .first()

                json?.let { Json.decodeFromString<AuthResponse>(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun clearUser() {
        withContext(Dispatchers.IO) {
            try {
                dataStore.edit { it.remove(AUTH_KEY) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}