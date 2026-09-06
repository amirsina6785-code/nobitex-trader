package com.nobitex.trader.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var api: TradingBotApi? = null

    fun create(
        baseUrl: String,
        key: String
    ): TradingBotApi {

        val url =
            if (baseUrl.endsWith("/")) {
                baseUrl
            } else {
                "$baseUrl/"
            }

        require(
            url.startsWith("https://") ||
            url.startsWith("http://")
        ) {
            "Server URL must start with http:// or https://"
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->

                val request = chain.request()
                    .newBuilder()
                    .header(
                        "Authorization",
                        "Bearer $key"
                    )
                    .header(
                        "Accept",
                        "application/json"
                    )
                    .header(
                        "Content-Type",
                        "application/json"
                    )
                    .build()

                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        api = Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(TradingBotApi::class.java)

        return api!!
    }

    fun current(): TradingBotApi {
        return api
            ?: throw IllegalStateException(
                "API is not connected"
            )
    }

    fun clear() {
        api = null
    }
}
