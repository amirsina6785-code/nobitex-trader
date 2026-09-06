package com.nobitex.trader.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.nobitex.trader.data.model.ActivityLog
import com.nobitex.trader.data.model.BotStatus
import com.nobitex.trader.data.model.Trade
import com.nobitex.trader.data.model.WalletBalance

@Composable
fun App(vm: TradingViewModel) {

    var loggedIn by remember {
        mutableStateOf(vm.hasSaved())
    }

    if (loggedIn) {

        LaunchedEffect(Unit) {
            if (vm.hasSaved()) {
                vm.connect(
                    vm.savedUrl(),
                    vm.savedKey()
                )
            }
        }

        DashboardScreen(
            vm = vm,
            onLogout = {
                vm.logout()
                loggedIn = false
            }
        )

    } else {

        LoginScreen(
            vm = vm,
            onConnected = {
                loggedIn = true
            }
        )
    }
}

@Composable
private fun LoginScreen(
    vm: TradingViewModel,
    onConnected: () -> Unit
) {

    var url by remember {
        mutableStateOf("")
    }

    var key by remember {
        mutableStateOf("")
    }

    val connection by vm.connection.collectAsState()

    LaunchedEffect(connection) {
        if (connection is UiState.Success) {
            onConnected()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Nobitex Trader",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "اتصال به سرور معامله‌گری",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("آدرس سرور") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("کلید کنترل") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { vm.connect(url, key) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("اتصال")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = connection) {
            UiState.Loading -> CircularProgressIndicator()

            is UiState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error
            )

            else -> Unit
        }
    }
}

@Composable
private fun DashboardScreen(
    vm: TradingViewModel,
    onLogout: () -> Unit
) {

    val status by vm.status.collectAsState()
    val wallet by vm.wallet.collectAsState()
    val trades by vm.trades.collectAsState()
    val logs by vm.logs.collectAsState()
    val message by vm.message.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nobitex Trader") },
                actions = {
                    OutlinedButton(onClick = onLogout) {
                        Text("خروج")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item { StatusCard(status) }
            item { WalletCard(wallet) }
            item { ActionButtons(vm) }

            item {
                Text(
                    text = "آخرین معاملات",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            when (val state = trades) {

                is UiState.Success -> {
                    items(state.value) { trade ->
                        TradeItem(trade)
                    }
                }

                UiState.Loading -> {
                    item {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    item {
                        Text(state.message)
                    }
                }

                else -> Unit
            }

            item {
                Text(
                    text = "گزارش فعالیت",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            when (val state = logs) {

                is UiState.Success -> {
                    items(state.value) { log ->
                        LogItem(log)
                    }
                }

                UiState.Loading -> {
                    item {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    item {
                        Text(state.message)
                    }
                }

                else -> Unit
            }

            if (message != null) {
                item {
                    Text(message ?: "")
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    state: UiState<BotStatus>
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "وضعیت ربات",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (state) {

                UiState.Loading -> {
                    Text("در حال دریافت...")
                }

                is UiState.Success -> {

                    Text(
                        text = if (state.value.isRunning)
                            "● در حال اجرا"
                        else
                            "● متوقف"
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("سرمایه: ${state.value.capital}")
                    Text("سود: ${state.value.profit}")
                    Text("درصد سود: ${state.value.profitPercent}%")
                    Text("تعداد معاملات: ${state.value.tradesCount}")
                }

                is UiState.Error -> {
                    Text(state.message)
                }

                else -> {
                    Text("وضعیت نامشخص")
                }
            }
        }
    }
}

@Composable
private fun WalletCard(
    state: UiState<WalletBalance>
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "کیف پول",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (state) {

                UiState.Loading -> {
                    Text("در حال همگام‌سازی...")
                }

                is UiState.Success -> {
                    Text("USDT: ${state.value.usdt}")
                    Text("BTC: ${state.value.btc}")
                    Text("ETH: ${state.value.eth}")
                    Text("USD: ${state.value.usd}")
                }

                is UiState.Error -> {
                    Text(state.message)
                }

                else -> {
                    Text("اطلاعات کیف پول موجود نیست")
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    vm: TradingViewModel
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = vm::syncWallet,
                modifier = Modifier.weight(1f)
            ) {
                Text("همگام‌سازی")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = vm::start,
                modifier = Modifier.weight(1f)
            ) {
                Text("شروع ربات")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                onClick = vm::stop,
                modifier = Modifier.weight(1f)
            ) {
                Text("توقف ربات")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = vm::emergency,
                modifier = Modifier.weight(1f)
            ) {
                Text("توقف اضطراری")
            }
        }
    }
}

@Composable
private fun TradeItem(
    trade: Trade
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text("${trade.symbol} - ${trade.type}")
            Text("قیمت: ${trade.price}")
            Text("مقدار: ${trade.amount}")
            Text("سود: ${trade.profit}")
        }
    }
}

@Composable
private fun LogItem(
    log: ActivityLog
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(log.message)

            if (log.level.isNotBlank()) {
                Text(
                    text = log.level,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
