package com.harshcode.currencyexchange

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.harshcode.currencyexchange.di.initializeKoin
import com.harshcode.currencyexchange.presentation.screen.HomeScreen

@Composable
@Preview
fun App() {
    initializeKoin()
    MaterialTheme {
        Navigator(HomeScreen())
    }
}