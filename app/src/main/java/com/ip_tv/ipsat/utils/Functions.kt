package com.zbekz.tashkentmetro.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.app.App
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.NetworkInterface
import java.util.Collections


fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


@Suppress("DEPRECATION")
fun Activity.hideSystemBars() {
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}

@Suppress("DEPRECATION")
fun Fragment.hideSystemBars() {
    requireActivity().window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}

fun setAnimation(
    context: Context,
    viewToAnimate: View,
    duration: Long = 150,
    list: FloatArray = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f),
    pivot: Pair<Float, Float> = 0.5f to 0.5f
) {
    val anim = ScaleAnimation(
        list[0],
        list[1],
        list[2],
        list[3],
        Animation.RELATIVE_TO_SELF,
        pivot.first,
        Animation.RELATIVE_TO_SELF,
        pivot.second
    )
    anim.duration = (duration * 1f).toLong()
    anim.setInterpolator(context, R.anim.over_shoot)
    viewToAnimate.startAnimation(anim)
}

fun Int?.or1() = this ?: 1

fun setSlideIn() = AnimationSet(false).apply {
    var animation: Animation = AlphaAnimation(0.0f, 1.0f)
    animation.duration = (500 * 1f).toLong()
    animation.interpolator = AccelerateDecelerateInterpolator()
    addAnimation(animation)

    animation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 1.0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, 0f
    )

    animation.duration = (750 * 1f).toLong()
    animation.interpolator = OvershootInterpolator(1.1f)
    addAnimation(animation)
}

fun setSlideUp() = AnimationSet(false).apply {
    var animation: Animation = AlphaAnimation(0.0f, 1.0f)
    animation.duration = (500 * 1f).toLong()
    animation.interpolator = AccelerateDecelerateInterpolator()
    addAnimation(animation)

    animation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 1.0f,
        Animation.RELATIVE_TO_SELF, 0f
    )

    animation.duration = (750 * 1f).toLong()
    animation.interpolator = OvershootInterpolator(1.1f)
    addAnimation(animation)
}


fun hasConnection(): Boolean {
    val connectivityManager =
        App.currentContext()!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}



private val PERMISSIONS_REQUEST_CODE = 100

fun requestPermissionsIfNecessary(context: Context) {
    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_WIFI_STATE
    )

    if (permissions.any { context.checkSelfPermission(it) != android.content.pm.PackageManager.PERMISSION_GRANTED }) {
        (context as Activity).requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }
}

@SuppressLint("HardwareIds")
private fun getMacAddressWithWifiManager(context: Context): String? {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    return try {
        val macAddress = wifiManager.connectionInfo.macAddress
        if (macAddress == "02:00:00:00:00:00") {
            null
        } else {
            macAddress
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getMacAddressWithNetworkInterface(): String? {
    return try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (networkInterface in interfaces) {
            if (networkInterface.name.equals("wlan0", ignoreCase = true)) {
                val macBytes = networkInterface.hardwareAddress ?: return null
                return macBytes.joinToString(separator = ":") { byte ->
                    String.format("%02X", byte)
                }
            }
        }
        null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun getMacAddressFromSystem(): String? {
    return try {
        val process = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.readLine()?.trim()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getAndroidId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

inline fun <reified T> String.toDataClass(): T {
    val gson = Gson()
    return gson.fromJson(this, T::class.java)
}


