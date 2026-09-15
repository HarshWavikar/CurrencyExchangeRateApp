package com.harshcode.currencyexchange.domain

import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.RequestState

interface CurrencyApiService {
    suspend fun getLatestExchangeRates() : RequestState<List<Currency>>
}