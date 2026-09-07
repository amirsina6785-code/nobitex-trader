@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun App(vm: TradingViewModel) {

    var loggedIn by remember {
        mutableStateOf(vm.hasSaved())
    }

    if (loggedIn) {

        LaunchedEffect(Unit) {
            if (vm.hasSaved()) {
                vm.connect(vm.savedToken())
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

    var token by remember {
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
            text = "اتصال مستقیم به نوبیتکس",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = token,
            onValueChange = {
                token = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("توکن API نوبیتکس")
            },
            visualTransformation =
                PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                vm.connect(token)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("اتصال به نوبیتکس")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = connection) {

            UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun DashboardScreen(
    vm: TradingViewModel,
    onLogout: () -> Unit
) {

    val wallet by vm.wallet.collectAsState()
    val message by vm.message.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Nobitex Trader")
                },
                actions = {
                    OutlinedButton(
                        onClick = onLogout
                    ) {
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
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "کیف پول نوبیتکس",
                            style =
                                MaterialTheme.typography.titleLarge
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        when (val state = wallet) {

                            UiState.Loading -> {
                                Text(
                                    "در حال دریافت موجودی..."
                                )
                            }

                            is UiState.Success -> {

                                state.value.wallets
                                    .forEach { (currency, data) ->

                                        Text(
                                            "$currency: ${data.balance}"
                                        )
                                    }
                            }

                            is UiState.Error -> {
                                Text(state.message)
                            }

                            else -> {
                                Text(
                                    "اطلاعات کیف پول موجود نیست"
                                )
                            }
                        }
                    }
                }
            }

            item {

                Button(
                    onClick = vm::syncWallet,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("همگام‌سازی کیف پول")
                }
            }

            if (message != null) {

                item {
                    Text(message ?: "")
                }
            }
        }
    }
}
