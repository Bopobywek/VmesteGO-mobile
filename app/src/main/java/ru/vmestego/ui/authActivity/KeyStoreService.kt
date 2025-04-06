package ru.vmestego.ui.authActivity

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher

class KeyStoreService {
    private val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_RSA,
        "AndroidKeyStore"
    )
    private lateinit var kp: KeyPair
    private var _alias: String = ""
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")

    private fun buildKeyPair(alias: String) {
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            build()
        }
        kpg.initialize(parameterSpec)
        kp = kpg.generateKeyPair()

    }

    fun encryptData(alias: String, data: String): ByteArray {
        if (alias != _alias) {
            buildKeyPair(alias)
            _alias = alias
        }

        cipher.init(Cipher.ENCRYPT_MODE, kp.public)
        val encryptedData = cipher.doFinal(data.toByteArray())
        Log.i("Enc", encryptedData.toString())

        return encryptedData
    }

    fun decryptData(alias: String, encryptedData: ByteArray) {
        if (alias != _alias) {
            buildKeyPair(alias)
            _alias = alias
        }

        cipher.init(Cipher.DECRYPT_MODE, kp.private)
        val decryptedData = cipher.doFinal(encryptedData)
        Log.i("Dec", decryptedData.toString())
    }
}