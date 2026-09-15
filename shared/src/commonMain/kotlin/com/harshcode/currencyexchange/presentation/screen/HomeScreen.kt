package com.harshcode.currencyexchange.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import com.harshcode.currencyexchange.data.remote.api.CurrencyApiServiceImpl
import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.CurrencyType
import com.harshcode.currencyexchange.domain.model.RequestState
import com.harshcode.currencyexchange.presentation.components.CurrencyPickerDialog
import com.harshcode.currencyexchange.presentation.components.HomeBody
import com.harshcode.currencyexchange.presentation.components.HomeHeader
import com.harshcode.currencyexchange.ui.surfaceColor


class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = this.koinScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateStatus
        val sourceCurrency by viewModel.sourceCurrency
        val targetCurrency by viewModel.targetCurrency
        val currencies = viewModel.allCurrencies

        var amount by rememberSaveable { mutableStateOf(0.0) }

        var selectedCurrencyType: CurrencyType by remember { mutableStateOf(CurrencyType.None) }

        var dialogOpened by remember { mutableStateOf(false) }

        if (dialogOpened && selectedCurrencyType != CurrencyType.None) {
            CurrencyPickerDialog(
                currencies = currencies,
                currencyType = selectedCurrencyType,
                onConfirmClick = { currencyCode ->
                    if (selectedCurrencyType is CurrencyType.Source) {
                        viewModel.HomeEvents(HomeUiEvent.SaveSourceCurrencyCode(code = currencyCode.name))
                    } else if (selectedCurrencyType is CurrencyType.Target) {
                        viewModel.HomeEvents(HomeUiEvent.SaveTargetCurrencyCode(code = currencyCode.name))
                    }
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false
                },
                onDismiss = {
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceColor),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeHeader(
                status = rateStatus,
                onRatesRefreshed = { viewModel.HomeEvents(HomeUiEvent.RefreshRates) },
                source = sourceCurrency,
                target = targetCurrency,
                onSwitchClick = { viewModel.HomeEvents(HomeUiEvent.SwitchCurrencies) },
                amount = amount,
                onAmountChanged = { amount = it },
                onCurrencyTypeSelected = { currencyType ->
                    selectedCurrencyType = currencyType
                    dialogOpened = true
                }
            )

            HomeBody(
                source = sourceCurrency,
                target = targetCurrency,
                amount = amount
            )
        }
    }
}
