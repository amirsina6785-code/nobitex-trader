package com.nobitex.trader.data.model

data class WalletBalance(
    val usd: Double = 0.0,
    val btc: Double = 0.0,
    val eth: Double = 0.0,
    val usdt: Double = 0.0
)

data class BotStatus(
    val isRunning: Boolean = false,
    val capital: Double = 0.0,
    val profit: Double = 0.0,
    val profitPercent: Double = 0.0,
    val tradesCount: Int = 0,
    val lastSyncTime: Long = 0L,
    val status: String = "IDLE"
)

data class Trade(
    val id: String = "",
    val symbol: String = "",
    val type: String = "",
    val price: Double = 0.0,
    val amount: Double = 0.0,
    val profit: Double = 0.0,
    val timestamp: Long = 0L
)

data class ActivityLog(
    val id: String = "",
    val message: String = "",
    val level: String = "",
    val timestamp: Long = 0L
)

data class NobitexWallet(
    val id: Int = 0,
    val balance: String = "0",
    val blocked: String = "0"
)

data class NobitexWalletResponse(
    val status: String = "",
    val wallets: Map<String, NobitexWallet> = emptyMap()
)

data class NobitexProfileResponse(
    val status: String = "",
    val profile: NobitexProfile? = null
)

data class NobitexProfile(
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)
