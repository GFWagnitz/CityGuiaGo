package com.pi.cityguiago.di

import com.pi.cityguiago.module.Attraction.AttractionService
import com.pi.cityguiago.module.Itinerary.ItineraryService
import com.pi.cityguiago.module.Itinerary.ItineraryViewModel
import com.pi.cityguiago.module.Login.LoginService
import com.pi.cityguiago.module.Login.LoginViewModel
import com.pi.cityguiago.module.Register.RegisterService
import com.pi.cityguiago.module.Register.RegisterViewModel
import com.pi.cityguiago.module.home.AttractionViewModel
import com.pi.cityguiago.module.home.ExploreViewModel
import com.pi.cityguiago.module.home.HomeService
import com.pi.cityguiago.module.home.HomeViewModel
import com.pi.cityguiago.network.ApiClient
import com.pi.cityguiago.network.PrefCacheManager
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore

fun sharedModule(engine: HttpClientEngine, dataStore: DataStore<Preferences>): Module = module {
    single { ApiClient(engine) }
    single { PrefCacheManager(dataStore) }
    single { RegisterService(get()) }
    factory { RegisterViewModel(get()) } // Changed to factory
    single { LoginService(get()) }
    factory { LoginViewModel(get()) } // Changed to factory
    single { HomeService(get()) }
    factory { HomeViewModel(get()) } // Changed to factory if needed
    factory { ExploreViewModel() } // Changed to factory if you want new instance every time
    single { AttractionService(get()) }
    factory { AttractionViewModel(get()) } // Changed to factory
    single { ItineraryService(get(), get<PrefCacheManager>()) }
    factory { ItineraryViewModel(get()) } // Changed to factory if needed
}