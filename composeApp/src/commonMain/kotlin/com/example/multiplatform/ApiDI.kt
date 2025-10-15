package com.example.multiplatform

import org.koin.dsl.module

val appModule = module {
    single { ApiService() }
    single { UserRepository(get()) }
}