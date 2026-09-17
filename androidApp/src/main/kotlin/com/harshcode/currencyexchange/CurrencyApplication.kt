package com.harshcode.currencyexchange

import android.app.Application
import com.harshcode.currencyexchange.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class CurrencyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(androidContext = this@CurrencyApplication)
        }
    }
}