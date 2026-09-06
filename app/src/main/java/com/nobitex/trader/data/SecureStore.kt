package com.nobitex.trader.data

import android.content.Context
import android.util.Base64
import com.nobitex.trader.data.api.ApiClient
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureStore(context: Context) {

    private val prefs =
        context.getSharedPreferences("trader_secure", Context.MODE_PRIVATE)

    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        val existingKey =
            keyStore.getKey("NobitexTraderKey", null) as? SecretKey

        if (existingKey != null) {
            return existingKey
        }

        return KeyGenerator
            .getInstance("AES", "AndroidKeyStore")
            .apply {
                init(256)
            }
            .generateKey()
    }

    fun save(url: String, token: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getKey()
        )

        val encrypted =
            cipher.iv + cipher.doFinal(token.toByteArray())

        prefs.edit()
            .putString("url", url)
            .putString(
                "token",
                Base64.encodeToString(encrypted, Base64.NO_WRAP)
            )
            .apply()
    }

    fun url(): String {
        return prefs.getString("url", "") ?: ""
    }

    fun token(): String {
        return runCatching {
            val data = Base64.decode(
                prefs.getString("token", "") ?: "",
                Base64.NO_WRAP
            )

            if (data.size < 13) {
                return@runCatching ""
            }

            val cipher =
                Cipher.getInstance("AES/GCM/NoPadding")

            cipher.init(
                Cipher.DECRYPT_MODE,
                getKey(),
                GCMParameterSpec(
                    128,
                    data.copyOfRange(0, 12)
                )
            )

            String(
                cipher.doFinal(
                    data.copyOfRange(12, data.size)
                )
            )
        }.getOrDefault("")
    }

    fun clear() {
        prefs.edit().clear().apply()
        ApiClient.clear()
    }
}
