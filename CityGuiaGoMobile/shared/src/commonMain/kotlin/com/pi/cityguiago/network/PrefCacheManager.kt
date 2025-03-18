package com.pi.cityguiago.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pi.cityguiago.model.AuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PrefCacheManager(
    private val dataStore: DataStore<Preferences>
) {
    private val AUTH_KEY = stringPreferencesKey("auth_response")
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val FAVORITE_ITINERARIES_KEY = stringPreferencesKey("favorite_itineraries")

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

    suspend fun saveToken(token: String) {
        dataStore.edit { pref ->
            pref[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { pref ->
            pref[TOKEN_KEY]
        }.firstOrNull()
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { pref ->
            pref[USER_NAME_KEY] = name
        }
    }

    suspend fun getUserName(): String? {
        return dataStore.data.map { pref ->
            pref[USER_NAME_KEY]
        }.firstOrNull()
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { pref ->
            pref[USER_EMAIL_KEY] = email
        }
    }

    suspend fun getUserEmail(): String? {
        return dataStore.data.map { pref ->
            pref[USER_EMAIL_KEY]
        }.firstOrNull()
    }

    suspend fun saveUserId(id: String) {
        dataStore.edit { pref ->
            pref[USER_ID_KEY] = id
        }
    }

    suspend fun getUserId(): String? {
        return dataStore.data.map { pref ->
            pref[USER_ID_KEY]
        }.firstOrNull()
    }
    
    suspend fun saveFavoriteItineraryIds(favorites: Set<String>) {
        dataStore.edit { pref ->
            pref[FAVORITE_ITINERARIES_KEY] = Json.encodeToString(favorites)
        }
    }
    
    suspend fun getFavoriteItineraryIds(): Set<String>? {
        return dataStore.data.map { pref ->
            pref[FAVORITE_ITINERARIES_KEY]?.let {
                Json.decodeFromString<Set<String>>(it)
            }
        }.firstOrNull()
    }
}