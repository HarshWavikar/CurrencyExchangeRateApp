package com.harshcode.currencyexchange.data.local

import com.harshcode.currencyexchange.database.CurrencyDatabase
import com.harshcode.currencyexchange.domain.LocalRepository
import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.RequestState
import kotlinx.coroutines.flow.Flow

class SqlDelightImpl: LocalRepository {

    private val database = CurrencyDatabase
    override suspend fun insertCurrencyData(currency: Currency) {
        TODO("Not yet implemented")
    }

    override fun getCurrencyData(): Flow<RequestState<Currency>> {
        TODO("Not yet implemented")
    }

    override suspend fun cleanUp() {
        TODO("Not yet implemented")
    }
}