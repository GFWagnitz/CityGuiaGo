package com.pi.cityguiago

import android.app.Application
import com.pi.cityguiago.di.sharedModule
import io.ktor.client.engine.android.Android
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CityGuiaGoApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            modules(appModule, sharedModule(Android.create()))
        }
    }

    private val appModule = module {
        // Android-specific dependencies go here
    }
} 