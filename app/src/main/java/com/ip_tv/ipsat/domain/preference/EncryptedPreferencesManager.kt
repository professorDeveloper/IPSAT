package com.ip_tv.ipsat.domain.preference

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPreferencesManager(context: Context) {

    private val prefsFileName = "secure_prefs"
    private val tokenKey = "auth_token"


    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()


    private val encryptedPreferences = EncryptedSharedPreferences.create(
        context,
        prefsFileName,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )


    fun saveToken(token: String) {
        encryptedPreferences.edit().putString(tokenKey, token).apply()
    }


    fun getToken(): String? {
        return encryptedPreferences.getString(tokenKey, null)
    }


    fun clearToken() {
        encryptedPreferences.edit().remove(tokenKey).apply()
    }
}