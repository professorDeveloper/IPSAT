package com.ip_tv.ipsat.domain.preference

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.UUID

class UserPreferenceManager(context: Context) {

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    var userUID: String
        get() = sharedPref.getString("userUID", null) ?: generateAndSaveUID()
        set(value) {
            sharedPref.edit().putString("userUID", value).apply()
        }

    private fun generateAndSaveUID(): String {
        val newUID = UUID.randomUUID().toString()
        sharedPref.edit().putString("userUID", newUID).apply()
        return newUID
    }



}