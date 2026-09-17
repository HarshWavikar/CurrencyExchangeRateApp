package com.harshcode.currencyexchange.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.harshcode.currencyexchange.database.CurrencyDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = CurrencyDatabase.Schema,
            name = "CurrecyDatabase.bd"
        )
    }
}