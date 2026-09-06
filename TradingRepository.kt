package com.nobitex.trader.data

import com.nobitex.trader.data.api.TradingBotApi
import com.nobitex.trader.data.model.*

class TradingRepository(
    private val apiProvider: () -> TradingBotApi
) {

    private fun api(): TradingBotApi = apiProvider()

    suspend fun connect(controlKey: String): Result<ConnectionData> = try {
        val response = api().connect(ConnectRequest(controlKey))

        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let {
                Result.success(it)
            } ?: Result.failure(Exception("پاسخ سرور خالی است"))
        } else {
            Result.failure(
                Exception(
                    response.body()?.error
                        ?: response.body()?.message
                        ?: "اتصال ناموفق بود"
                )
            )
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getBotStatus(): Result<BotStatus> = try {
        val response = api().getBotStatus()

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(response.body()?.data ?: BotStatus())
        } else {
            Result.failure(Exception("دریافت وضعیت ربات ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun syncWallet(): Result<WalletBalance> = try {
        val response = api().syncWallet()

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(response.body()?.data ?: WalletBalance())
        } else {
            Result.failure(Exception("همگام‌سازی کیف پول ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getWalletBalance(): Result<WalletBalance> = try {
        val response = api().getWalletBalance()

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(response.body()?.data ?: WalletBalance())
        } else {
            Result.failure(Exception("دریافت موجودی ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun allocateCapital(
        amount: Double,
        symbol: String = "USDT"
    ): Result<Unit> = try {
        val response = api().allocateCapital(
            AllocateRequest(amount, symbol)
        )

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("تخصیص سرمایه ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun startBot(): Result<Unit> = try {
        val response = api().startBot()

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("شروع ربات ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun stopBot(): Result<Unit> = try {
        val response = api().stopBot()

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("توقف ربات ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun emergencyStop(): Result<Unit> = try {
        val response = api().emergencyStop()

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("توقف اضطراری ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getTrades(): Result<List<Trade>> = try {
        val response = api().getTrades()

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(response.body()?.data ?: emptyList())
        } else {
            Result.failure(Exception("دریافت معاملات ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getLogs(): Result<List<ActivityLog>> = try {
        val response = api().getLogs()

        if (response.isSuccessful && response.body()?.success == true) {
            Result.success(response.body()?.data ?: emptyList())
        } else {
            Result.failure(Exception("دریافت گزارش‌ها ناموفق بود"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
