package com.nobitex.trader.data

import com.nobitex.trader.data.api.TradingBotApi
import com.nobitex.trader.data.model.NobitexWalletResponse

class TradingRepository(
    private val apiProvider: () -> TradingBotApi
) {

    private fun api(): TradingBotApi = apiProvider()

    suspend fun getProfile(): Result<Unit> {
        return try {
            val response = api().getProfile()

            if (response.isSuccessful &&
                response.body()?.status == "ok"
            ) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception(
                        response.body()?.status
                            ?: "دریافت اطلاعات حساب ناموفق بود"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWallets(): Result<NobitexWalletResponse> {
        return try {
            val response = api().getWallets()

            if (response.isSuccessful &&
                response.body()?.status == "ok"
            ) {
                val data = response.body()

                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(
                        Exception("پاسخ کیف پول خالی است")
                    )
                }
            } else {
                Result.failure(
                    Exception("دریافت کیف پول ناموفق بود")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
