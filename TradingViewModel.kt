package com.nobitex.trader.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nobitex.trader.data.SecureStore
import com.nobitex.trader.data.TradingRepository
import com.nobitex.trader.data.api.ApiClient
import com.nobitex.trader.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val value: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class TradingViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val store = SecureStore(app)

    private val repo = TradingRepository {
        ApiClient.current()
    }

    val connection =
        MutableStateFlow<UiState<ConnectionData>>(UiState.Idle)

    val status =
        MutableStateFlow<UiState<BotStatus>>(UiState.Idle)

    val wallet =
        MutableStateFlow<UiState<WalletBalance>>(UiState.Idle)

    val trades =
        MutableStateFlow<UiState<List<Trade>>>(UiState.Idle)

    val logs =
        MutableStateFlow<UiState<List<ActivityLog>>>(UiState.Idle)

    val message =
        MutableStateFlow<String?>(null)

    fun connect(url: String, key: String) {

        if (url.isBlank() || key.isBlank()) {
            connection.value =
                UiState.Error("آدرس سرور و کلید کنترل را وارد کنید.")
            return
        }

        viewModelScope.launch {

            connection.value = UiState.Loading

            try {
                ApiClient.create(
                    url.trim(),
                    key
                )

                repo.connect(key)
                    .onSuccess {

                        store.save(
                            url.trim(),
                            key
                        )

                        connection.value =
                            UiState.Success(it)

                        refreshAll()
                    }
                    .onFailure {

                        ApiClient.clear()
                        connection.value =
                            UiState.Error(
                                it.message ?: "اتصال برقرار نشد."
                            )
                    }

            } catch (e: Exception) {

                ApiClient.clear()

                connection.value =
                    UiState.Error(
                        e.message ?: "خطا در اتصال به سرور."
                    )
            }
        }
    }

    fun refreshAll() {
        refreshStatus()
        syncWallet()
        loadTrades()
        loadLogs()
    }

    fun refreshStatus() {

        viewModelScope.launch {

            status.value = UiState.Loading

            repo.getBotStatus()
                .onSuccess {
                    status.value =
                        UiState.Success(it)
                }
                .onFailure {
                    status.value =
                        UiState.Error(
                            it.message ?: "خطا در دریافت وضعیت ربات."
                        )
                }
        }
    }

    fun syncWallet() {

        viewModelScope.launch {

            wallet.value = UiState.Loading

            repo.syncWallet()
                .onSuccess {
                    wallet.value =
                        UiState.Success(it)

                    message.value =
                        "کیف پول همگام شد."
                }
                .onFailure {
                    wallet.value =
                        UiState.Error(
                            it.message ?: "خطا در همگام‌سازی کیف پول."
                        )
                }
        }
    }

    fun allocate(amount: Double) {

        if (amount <= 0) {
            message.value =
                "مبلغ باید بیشتر از صفر باشد."
            return
        }

        viewModelScope.launch {

            repo.allocateCapital(amount)
                .onSuccess {
                    message.value =
                        "سرمایه تخصیص داده شد."

                    refreshStatus()
                }
                .onFailure {
                    message.value =
                        it.message ?: "تخصیص سرمایه ناموفق بود."
                }
        }
    }

    fun start() {
        action("ربات اجرا شد.") {
            repo.startBot()
        }
    }

    fun stop() {
        action("ربات متوقف شد.") {
            repo.stopBot()
        }
    }

    fun emergency() {
        action("توقف اضطراری انجام شد.") {
            repo.emergencyStop()
        }
    }

    private fun action(
        successMessage: String,
        block: suspend () -> Result<Unit>
    ) {

        viewModelScope.launch {

            block()
                .onSuccess {
                    message.value =
                        successMessage

                    refreshStatus()
                }
                .onFailure {
                    message.value =
                        it.message ?: "عملیات ناموفق بود."
                }
        }
    }

    fun loadTrades() {

        viewModelScope.launch {

            repo.getTrades()
                .onSuccess {
                    trades.value =
                        UiState.Success(it)
                }
                .onFailure {
                    trades.value =
                        UiState.Error(
                            it.message ?: "خطا در دریافت معاملات."
                        )
                }
        }
    }

    fun loadLogs() {

        viewModelScope.launch {

            repo.getLogs()
                .onSuccess {
                    logs.value =
                        UiState.Success(it)
                }
                .onFailure {
                    logs.value =
                        UiState.Error(
                            it.message ?: "خطا در دریافت گزارش‌ها."
                        )
                }
        }
    }

    fun logout() {

        store.clear()

        connection.value = UiState.Idle
        status.value = UiState.Idle
        wallet.value = UiState.Idle
        trades.value = UiState.Idle
        logs.value = UiState.Idle
    }

    fun savedUrl(): String {
        return store.url()
    }

    fun savedKey(): String {
        return store.token()
    }

    fun hasSaved(): Boolean {
        return savedUrl().isNotBlank() &&
                savedKey().isNotBlank()
    }
}
