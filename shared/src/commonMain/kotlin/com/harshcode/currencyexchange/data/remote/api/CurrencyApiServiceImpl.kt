package com.harshcode.currencyexchange.data.remote.api

import com.harshcode.currencyexchange.BuildKonfig
import com.harshcode.currencyexchange.domain.CurrencyApiService
import com.harshcode.currencyexchange.domain.PreferenceRepository
import com.harshcode.currencyexchange.domain.model.ApiResponse
import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.CurrencyCode
import com.harshcode.currencyexchange.domain.model.RequestState
import com.harshcode.currencyexchange.domain.model.toCurrency
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl(
    private val preference: PreferenceRepository
) : CurrencyApiService {

    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        val API_KEY = BuildKonfig.API_KEY
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }

        install(DefaultRequest) {
            header("apikey", API_KEY)
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<Currency>> {
        return try {
            val response = httpClient.get(ENDPOINT)
            if (response.status.value == 200) {
                val apiResponse = Json.decodeFromString<ApiResponse>(response.body())

                val availableCurrencyCode = apiResponse.data.keys
                    .filter {
                        CurrencyCode.entries
                            .map {code -> code.name }
                            .toSet()
                            .contains(it)
                    }

                val availableCurrencies = apiResponse.data.values
                    .filter {currency ->
                        availableCurrencyCode.contains(currency.code)
                    }.map {
                        it.toCurrency()
                    }

                val lastUpdated = apiResponse.meta.lastUpdatedAt
                preference.saveLastUpdated(lastUpdated = lastUpdated)

                RequestState.Success(data = availableCurrencies)
            } else {
                RequestState.Error("Http error code: ${response.status}")
            }
        } catch (e: Exception) {
            RequestState.Error("Error: ${e.message}")
        }
    }
}