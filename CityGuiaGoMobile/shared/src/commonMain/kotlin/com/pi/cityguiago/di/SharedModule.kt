package com.pi.cityguiago.di

import com.pi.cityguiago.module.Attraction.AttractionService
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
    single { RegisterViewModel(get()) }
    single { LoginService(get()) }
    single { LoginViewModel(get()) }
    single { HomeService(get()) }
    single { HomeViewModel(get()) }
    single { ExploreViewModel() }
    single { AttractionService(get()) }
    single { AttractionViewModel(get()) }
} 