package com.harshcode.currencyexchange.di

import com.harshcode.currencyexchange.data.local.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory() }
}