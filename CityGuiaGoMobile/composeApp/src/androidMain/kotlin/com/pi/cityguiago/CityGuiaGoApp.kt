package com.pi.cityguiago

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pi.cityguiago.di.sharedModule
import io.ktor.client.engine.android.Android
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "city_guide_prefs")

class CityGuiaGoApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            modules(appModule, sharedModule(Android.create(), dataStore))
        }
    }

    private val appModule = module {
        // Android-specific dependencies go here
    }
} 