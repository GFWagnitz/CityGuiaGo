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
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

fun sharedModule(engine: HttpClientEngine): Module = module {
    single { ApiClient(engine) }
    single { RegisterService(get()) }
    factory { RegisterViewModel(get()) } // Changed to factory
    single { LoginService(get()) }
    factory { LoginViewModel(get()) } // Changed to factory
    single { HomeService(get()) }
    factory { HomeViewModel(get()) } // Changed to factory if needed
    factory { ExploreViewModel() } // Changed to factory if you want new instance every time
    single { AttractionService(get()) }
    factory { AttractionViewModel(get()) } // Changed to factory
    single { ItineraryService(get()) }
    factory { ItineraryViewModel(get()) } // Changed to factory if needed
}