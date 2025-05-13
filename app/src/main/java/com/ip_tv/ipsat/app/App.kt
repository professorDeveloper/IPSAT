package com.ip_tv.ipsat.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.bugsnag.android.Bugsnag
import com.bugsnag.android.performance.BugsnagPerformance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
@SuppressLint("StaticFieldLeak")
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Bugsnag.start(this)
        BugsnagPerformance.start(this)

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