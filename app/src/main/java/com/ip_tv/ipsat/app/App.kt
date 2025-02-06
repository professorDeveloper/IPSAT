package com.ip_tv.ipsat.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
@SuppressLint("StaticFieldLeak")
class App : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    init {
        instance = this
    }



    companion object {
        var instance: App? = null
        var context: Context? = null
        fun currentContext(): Context? {
            return  instance!!
        }


    }
}