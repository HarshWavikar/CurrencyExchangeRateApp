package com.harshcode.currencyexchange.domain

import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.RequestState
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun insertCurrencyData(currency: Currency)
    fun readCurrencyData(): Flow<RequestState<List<Currency>>>
    suspend  fun cleanUp()
}