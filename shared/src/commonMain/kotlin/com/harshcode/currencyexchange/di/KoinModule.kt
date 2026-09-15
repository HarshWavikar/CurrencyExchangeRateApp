package com.harshcode.currencyexchange.di

import com.harshcode.currencyexchange.data.local.PreferencesImpl
import com.harshcode.currencyexchange.data.remote.api.CurrencyApiServiceImpl
import com.harshcode.currencyexchange.domain.CurrencyApiService
import com.harshcode.currencyexchange.domain.PreferenceRepository
import com.harshcode.currencyexchange.presentation.screen.HomeViewModel
import com.russhwolf.settings.Settings
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

val appModule = module {
    single { Settings() }
    single<PreferenceRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preference = get()) }
    factory {
        HomeViewModel(
            preferences = get(),
            api = get()
        )
    }
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
){
    if (KoinPlatformTools.defaultContext().getOrNull() == null) {
        startKoin {
            config?.invoke(this)
            modules(appModule)
        }
    }
}