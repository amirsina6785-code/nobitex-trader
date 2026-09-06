package com.nobitex.trader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.nobitex.trader.ui.App
import com.nobitex.trader.ui.TradingViewModel
import com.nobitex.trader.ui.theme.NobitexTraderTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TradingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NobitexTraderTheme {
                App(viewModel)
            }
        }
    }
}
