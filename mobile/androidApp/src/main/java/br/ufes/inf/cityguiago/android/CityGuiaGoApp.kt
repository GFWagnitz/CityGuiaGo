package br.ufes.inf.cityguiago.android

import android.app.Application
import br.ufes.inf.cityguiago.di.sharedModule
import io.ktor.client.engine.android.Android
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CityGuiaGoApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@CityGuiaGoApp)
            modules(appModule, sharedModule(Android.create()))
        }
    }
    
    private val appModule = module {
        // Android-specific dependencies go here
    }
} 