package com.nobitex.trader.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class NobitexProfileResponse(
    val status: String = "",
    val profile: Profile? = null
)

data class Profile(
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

data class NobitexWalletResponse(
    val status: String = "",
    val wallets: Map<String, WalletItem>? = null
)

data class WalletItem(
    val balance: String? = null,
    val blockedBalance: String? = null
)

interface TradingBotApi {

    @GET("users/profile")
    suspend fun getProfile(): Response<NobitexProfileResponse>

    @GET("v2/wallets")
    suspend fun getWallets(): Response<NobitexWalletResponse>
}
