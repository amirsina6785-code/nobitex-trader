package com.nobitex.trader.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nobitex.trader.data.SecureStore
import com.nobitex.trader.data.TradingRepository
import com.nobitex.trader.data.api.ApiClient
import com.nobitex.trader.data.model.NobitexWalletResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val value: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class TradingViewModel(app: Application) : AndroidViewModel(app) {

    private val store = SecureStore(app)

    private val repo = TradingRepository {
        ApiClient.current()
    }

    val connection = MutableStateFlow<UiState<Unit>>(UiState.Idle)

    val wallet =
        MutableStateFlow<UiState<NobitexWalletResponse>>(UiState.Idle)

    val message = MutableStateFlow<String?>(null)

    fun connect(token: String) {

        if (token.isBlank()) {
            connection.value =
                UiState.Error("توکن API نوبیتکس را وارد کنید.")
            return
        }

        viewModelScope.launch {

            connection.value = UiState.Loading

            try {

                ApiClient.create(token.trim())

                repo.getProfile()
                    .onSuccess {

                        store.save(
                            "https://api.nobitex.ir/",
                            token.trim()
                        )

                        connection.value =
                            UiState.Success(Unit)

                        syncWallet()
                    }
                    .onFailure {

                        ApiClient.clear()

                        connection.value =
                            UiState.Error(
                                it.message
                                    ?: "اتصال به نوبیتکس برقرار نشد."
                            )
                    }

            } catch (e: Exception) {

                ApiClient.clear()

                connection.value =
                    UiState.Error(
                        e.message
                            ?: "خطا در اتصال به نوبیتکس."
                    )
            }
        }
    }

    fun syncWallet() {

        viewModelScope.launch {

            wallet.value = UiState.Loading

            repo.getWallets()
                .onSuccess {

                    wallet.value =
                        UiState.Success(it)

                    message.value =
                        "کیف پول نوبیتکس همگام شد."
                }
                .onFailure {

                    wallet.value =
                        UiState.Error(
                            it.message
                                ?: "دریافت کیف پول ناموفق بود."
                        )
                }
        }
    }

    fun savedToken(): String =
        store.token()

    fun hasSaved(): Boolean =
        savedToken().isNotBlank()

    fun logout() {

        store.clear()

        connection.value = UiState.Idle
        wallet.value = UiState.Idle
        message.value = null
    }
}
