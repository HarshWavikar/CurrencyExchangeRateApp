package com.harshcode.currencyexchange.data.local

import app.cash.sqldelight.Query
import com.harshcode.currencyexchange.database.CurrencyDatabase
import com.harshcode.currencyexchange.domain.LocalRepository
import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class SqlDelightImpl(
    databaseDriverFactory: DatabaseDriverFactory
) : LocalRepository {

    private val database = CurrencyDatabase(driver = databaseDriverFactory.createDriver())
    val queries = database.currencyDatabaseQueries

    override suspend fun insertCurrencyData(currency: Currency) {
        withContext(Dispatchers.IO) {
            queries.insertCurrency(currency.code, value_ = currency.value)
        }
    }

    override fun readCurrencyData(): Flow<RequestState<List<Currency>>> = callbackFlow {
        val listener = Query.Listener {
            try {
                val currencyList = queries.getAllCurrencies().executeAsList()
                val currencies = currencyList.map { currencyTable ->
                    Currency(
                        id = currencyTable.id,
                        code = currencyTable.code,
                        value = currencyTable.value_
                    )
                }
                trySend(RequestState.Success(data = currencies))
            } catch (e: Exception) {
                trySend(RequestState.Error(errorMessage = e.message ?: "Unknown Error"))
            }
        }
        val query = queries.getAllCurrencies()
        query.addListener(listener = listener)
        listener.queryResultsChanged()

        awaitClose {
            query.removeListener(listener = listener)
        }
    }

    override suspend fun cleanUp() {
        withContext(Dispatchers.IO) {
            queries.deleteAllCurrencies()
        }
    }
}