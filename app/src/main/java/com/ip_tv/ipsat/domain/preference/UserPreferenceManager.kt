package com.ip_tv.ipsat.domain.preference

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class UserPreferenceManager @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    var isLogged :Boolean
        get() = sharedPref.getBoolean("isLogged", false)
        set(value) {
            sharedPref.edit().putBoolean("isLogged", value).apply()
        }

    var subCode: String
        get() = sharedPref.getString("activate_code", null) ?: generateAndSaveUID()
        set(value) {
            sharedPref.edit().putString("activate_code", value).apply()
        }

    private fun generateAndSaveUID(): String {
        val newUID = UUID.randomUUID().toString()
        sharedPref.edit().putString("userUID", newUID).apply()
        return newUID
    }



}