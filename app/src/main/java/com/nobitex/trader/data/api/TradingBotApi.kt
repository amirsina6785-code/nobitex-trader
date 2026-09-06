package com.nobitex.trader.data.api

import com.nobitex.trader.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface TradingBotApi {

    @POST("api/bot/connect")
    suspend fun connect(
        @Body request: ConnectRequest
    ): Response<ServerResponse<ConnectionData>>

    @GET("api/bot/status")
    suspend fun getBotStatus(): Response<ServerResponse<BotStatus>>

    @POST("api/wallet/sync")
    suspend fun syncWallet(): Response<ServerResponse<WalletBalance>>

    @POST("api/wallet/balance")
    suspend fun getWalletBalance(): Response<ServerResponse<WalletBalance>>

    @POST("api/bot/allocate")
    suspend fun allocateCapital(
        @Body request: AllocateRequest
    ): Response<ServerResponse<Any>>

    @POST("api/bot/start")
    suspend fun startBot(): Response<ServerResponse<Any>>

    @POST("api/bot/stop")
    suspend fun stopBot(): Response<ServerResponse<Any>>

    @POST("api/bot/emergency-stop")
    suspend fun emergencyStop(): Response<ServerResponse<Any>>

    @GET("api/bot/trades")
    suspend fun getTrades(
        @Query("limit") limit: Int = 50
    ): Response<ServerResponse<List<Trade>>>

    @GET("api/bot/logs")
    suspend fun getLogs(
        @Query("limit") limit: Int = 100
    ): Response<ServerResponse<List<ActivityLog>>>
}
