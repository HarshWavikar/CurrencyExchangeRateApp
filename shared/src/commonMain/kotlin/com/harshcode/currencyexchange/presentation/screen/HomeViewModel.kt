package com.harshcode.currencyexchange.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.harshcode.currencyexchange.domain.CurrencyApiService
import com.harshcode.currencyexchange.domain.LocalRepository
import com.harshcode.currencyexchange.domain.PreferenceRepository
import com.harshcode.currencyexchange.domain.model.Currency
import com.harshcode.currencyexchange.domain.model.RateStatus
import com.harshcode.currencyexchange.domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock

sealed class HomeUiEvent() {
    data object RefreshRates : HomeUiEvent()
    data object SwitchCurrencies : HomeUiEvent()
    data class SaveSourceCurrencyCode(val code: String) : HomeUiEvent()
    data class SaveTargetCurrencyCode(val code: String) : HomeUiEvent()
}

class HomeViewModel(
    private val preferences: PreferenceRepository,
    private val localDb: LocalRepository,
    private val api: CurrencyApiService
) : ScreenModel {

    private var _ratesStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus = _ratesStatus

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrencies: List<Currency> = _allCurrencies

    private var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
        }
    }

    private fun readSourceCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readSourceCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _sourceCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _sourceCurrency.value =
                        RequestState.Error(errorMessage = "Couldn't find the selected currency.")
                }
            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch() {
            preferences.readTargetCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _targetCurrency.value =
                        RequestState.Error(errorMessage = "Couldn't find the selected currency.")
                }
            }
        }
    }

    private suspend fun fetchNewRates() {
        try {
            val localCache = localDb.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("HomeViewModel: Database is full ")
                    _allCurrencies.clear()
                    _allCurrencies.addAll(localCache.getSuccessData())
                    if (!preferences.isDataFresh(Clock.System.now().toEpochMilliseconds())) {
                        println("HomeViewModel: Data not fresh ❌...")
                        cacheData()
                    } else {
                        println("HomeViewModel: Data is fresh ✅...")
                    }
                } else {
                    println("HomeViewModel: Database needs data")
                    cacheData()
                }
            }else if (localCache.isError()){
                println("HomeViewModel: Error reading local database - ${localCache.getError()}")
            }
            getRateStatus()
        } catch (e: Exception) {
            println("HomeViewModel: ${e.message}")
        }
    }

    private suspend fun cacheData() {
        val fetchData = api.getLatestExchangeRates()
        if (fetchData.isSuccess()) {
            localDb.cleanUp()
            fetchData.getSuccessData().forEach {
                println("HomeViewModel: Adding - ${it.code}")
                localDb.insertCurrencyData(it)
            }
            println("HomeViewModel: Updating _allCurrencies")
            _allCurrencies.clear()
            _allCurrencies.addAll(fetchData.getSuccessData())
        } else if (fetchData.isError()) {
            println("HomeViewModel: Fetching Failed ${fetchData.getError()}")
        }
    }

    private suspend fun getRateStatus() {
        _ratesStatus.value =
            if (preferences.isDataFresh(
                    currentTimeStamp = Clock.System.now().toEpochMilliseconds()
                )
            )
                RateStatus.Fresh else RateStatus.Stale
    }

    fun HomeEvents(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }

            HomeUiEvent.SwitchCurrencies -> {
                switchCurrencies()
            }

            is HomeUiEvent.SaveSourceCurrencyCode -> {
                saveSourceCurrencyCode(code = event.code)
            }

            is HomeUiEvent.SaveTargetCurrencyCode -> {
                saveTargetCurrencyCode(code = event.code)
            }
        }
    }

    private fun saveSourceCurrencyCode(code: String) {
        screenModelScope.launch(Dispatchers.IO) {
            preferences.saveSourceCurrencyCode(code = code)
        }
    }

    private fun saveTargetCurrencyCode(code: String) {
        screenModelScope.launch(Dispatchers.IO) {
            preferences.saveTargetCurrencyCode(code = code)
        }
    }

    private fun switchCurrencies() {
        val source = _sourceCurrency.value
        val target = _targetCurrency.value

        _sourceCurrency.value = target
        _targetCurrency.value = source
    }
}