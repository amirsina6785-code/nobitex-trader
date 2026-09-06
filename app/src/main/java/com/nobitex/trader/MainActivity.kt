package com.nobitex.trader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.nobitex.trader.data.TradingRepository
import com.nobitex.trader.data.api.ApiClient
import com.nobitex.trader.ui.TradingViewModel
import com.nobitex.trader.ui.AppNavigation
import com.nobitex.trader.ui.theme.NobitexTraderTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = TradingRepository(
            apiProvider = {
                if (!ApiClient.isInitialized()) {
                    throw IllegalStateException(
                        "API is not initialized. Connect to the server first."
                    )
                }
                ApiClient.getApi()
            }
        )

        val viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    return TradingViewModel(repository) as T
                }
            }
        )[TradingViewModel::class.java]

        setContent {
            NobitexTraderTheme {
                AppNavigation(viewModel)
            }
        }
    }
}
