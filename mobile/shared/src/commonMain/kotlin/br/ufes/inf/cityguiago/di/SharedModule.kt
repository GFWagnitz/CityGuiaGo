package br.ufes.inf.cityguiago.di

import br.ufes.inf.cityguiago.network.ApiClient
import br.ufes.inf.cityguiago.repository.AuthRepository
import br.ufes.inf.cityguiago.repository.AttractionRepository
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

fun sharedModule(engine: HttpClientEngine): Module = module {
    single { ApiClient(engine) }
    single { AuthRepository(get()) }
    single { AttractionRepository(get()) }
} 