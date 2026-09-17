package com.harshcode.currencyexchange

import androidx.compose.ui.window.ComposeUIViewController
import com.harshcode.currencyexchange.di.initializeKoin

fun MainViewController() = ComposeUIViewController (
    configure = { initializeKoin() }
){ App() }