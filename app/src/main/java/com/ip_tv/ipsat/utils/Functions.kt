package com.ip_tv.ipsat.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources.getSystem
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.app.App
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.domain.model.SubCategoryItem
import com.ip_tv.ipsat.presentation.activities.MainActivity
import com.ip_tv.ipsat.presentation.activities.SplashActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.NetworkInterface
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask
import kotlin.math.max

var statusBarHeight = 0
var navBarHeight = 0
val Int.dp: Float get() = (this / getSystem().displayMetrics.density)
val Float.px: Int get() = (this * getSystem().displayMetrics.density).toInt()

fun ArrayList<SubCategoryItem>.filterChannelsByCategory(
    categoryId: Int
): ArrayList<SubCategoryItem> {
    return this.filter { it.category.toInt() == categoryId } as ArrayList<SubCategoryItem>
}

fun ArrayList<ChannelResponseItem>.filterByKeywords(
    keywords: List<SubCategoryItem>
): List<ChannelResponseItem> {
    Log.d("GG", "filterByKeywords:${keywords} ")
    Log.d("GG", "filterByKeywords:${this} ")
    return this.filter { item ->
        keywords.any { keyword ->
            if (item.categoryProperty == null) false else item.categoryProperty.contains(
                keyword.property_name,
                ignoreCase = true
            )
        }
    }
}


suspend fun View.pop() {
    ObjectAnimator.ofFloat(this@pop, "scaleX", 1f, 1.25f).setDuration(120).start()
    ObjectAnimator.ofFloat(this@pop, "scaleY", 1f, 1.25f).setDuration(120).start()
    delay(120)
    ObjectAnimator.ofFloat(this@pop, "scaleX", 1.25f, 1f).setDuration(100).start()
    ObjectAnimator.ofFloat(this@pop, "scaleY", 1.25f, 1f).setDuration(100).start()
    delay(100)
}


fun openLinkInBrowser(link: String?, activity: Activity) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    activity.startActivity(intent)
}

fun String.toReadableDateTime(): String {
    return try {
        val instant = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        instant.format(formatter)
    } catch (e: Exception) {
        "Wrong format"
    }
}

fun String.toYear(): String {
    val year = this.substringBefore("-")
    return year
}



fun <T> readData(fileName: String, context: Context? = null, toast: Boolean = true): T? {
    val a = context ?: currContext()
    try {
        if (a?.fileList() != null)
            if (fileName in a.fileList()) {
                val fileIS: FileInputStream = a.openFileInput(fileName)
                val objIS = ObjectInputStream(fileIS)
                val data = objIS.readObject() as T
                objIS.close()
                fileIS.close()
                return data
            }
    } catch (e: Exception) {
        if (toast) snackString("Error loading data $fileName")
        e.printStackTrace()
    }
    return null
}

fun saveData(fileName: String, data: Any?, context: Context? = null) {
    tryWith {
        val a = context ?: currContext()
        if (a != null) {
            val fos: FileOutputStream = a.openFileOutput(fileName, Context.MODE_PRIVATE)
            val os = ObjectOutputStream(fos)
            os.writeObject(data)
            os.close()
            fos.close()
        }
    }
}

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


fun View.circularReveal(ex: Int, ey: Int, subX: Boolean, time: Long) {
    ViewAnimationUtils.createCircularReveal(
        this,
        if (subX) (ex - x.toInt()) else ex,
        ey - y.toInt(),
        0f,
        max(height, width).toFloat()
    ).setDuration(time).start()
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


suspend fun extractTarFile(tarUrl: String, outputDir: File) {
    withContext(Dispatchers.IO) {
        try {
            // Open the URL stream and wrap it in a TarArchiveInputStream
            URL(tarUrl).openStream().use { inputStream ->
                TarArchiveInputStream(BufferedInputStream(inputStream)).use { tarInputStream ->
                    var entry: TarArchiveEntry?

                    // Iterate through each entry in the .tar file
                    while (tarInputStream.nextTarEntry.also { entry = it } != null) {
                        val outputFile = File(outputDir, entry!!.name)

                        if (entry!!.isDirectory) {
                            // Create directories for folder entries
                            outputFile.mkdirs()
                        } else {
                            // Ensure parent directories are created
                            outputFile.parentFile?.mkdirs()

                            // Write the file content using a buffered output stream
                            BufferedOutputStream(FileOutputStream(outputFile)).use { outputStream ->
                                tarInputStream.copyTo(outputStream)
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to extract TAR file: ${e.message}")
        }
    }
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
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
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

fun <T> tryWith(post: Boolean = false, snackbar: Boolean = true, call: () -> T): T? {
    return try {
        call.invoke()
    } catch (e: Throwable) {
        null
    }
}


fun ImageView.loadImage(file: String?, size: Int = 0) {
    tryWith {
        val glideUrl = GlideUrl(file)
        Glide.with(this.context).load(glideUrl)
            .transition(DrawableTransitionOptions.withCrossFade()).override(size).into(this)
    }
}

fun View.preventTwoClick() {
    this.isEnabled = false
    this.postDelayed({
        isEnabled = true
    }, 300)
}

fun loadFilterTab(
    defaultItemCountry: String,
    defaultItemSort: String,
): ArrayList<FilterTabModel> {
    val list = ArrayList<FilterTabModel>()
    list.add(FilterTabModel(LocalData.country, "Country", defaultItemCountry))




    list.add(
        FilterTabModel(
            LocalData.rating,
            "Rating",
            defaultItemSort,
        )
    )

    return list
}


class MediaPageTransformer : ViewPager2.PageTransformer {
    private fun parallax(view: View, position: Float) {
        if (position > -1 && position < 1) {
            val width = view.width.toFloat()
            view.translationX = -(position * width * 0.8f)
        }
    }

    override fun transformPage(view: View, position: Float) {

        val bannerContainer = view.findViewById<View>(R.id.itemCompactBanner)
        parallax(bannerContainer, position)
    }
}


fun initActivity(a: Activity) {
    val window = a.window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    manageThemeAndRefresh(readData<Int>("current_theme", a) ?: 0)

}


fun startMainActivity(activity: Activity, bundle: Bundle? = null) {
    activity.finishAffinity()
    activity.startActivity(
        Intent(
            activity,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            if (bundle != null) putExtras(bundle)
        }
    )
}

fun manageThemeAndRefresh(them: Int) {
    AppCompatDelegate.setDefaultNightMode(
        when (them) {
            0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_YES
        }
    )
}

abstract class GesturesListener : GestureDetector.SimpleOnGestureListener() {
    private var timer: Timer? = null //at class level;
    private val delay: Long = 200

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        processSingleClickEvent(e)
        return super.onSingleTapUp(e)
    }

    override fun onLongPress(e: MotionEvent) {
        processLongClickEvent(e)
        super.onLongPress(e)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        processDoubleClickEvent(e)
        return super.onDoubleTap(e)
    }


    private fun processSingleClickEvent(e: MotionEvent) {
        val handler = Handler(Looper.getMainLooper())
        val mRunnable = Runnable {
            onSingleClick(e)
        }
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    handler.post(mRunnable)
                }
            }, delay)
        }
    }

    private fun processDoubleClickEvent(e: MotionEvent) {
        timer?.apply {
            cancel()
            purge()
        }
        onDoubleClick(e)
    }

    private fun processLongClickEvent(e: MotionEvent) {
        timer?.apply {
            cancel()
            purge()
        }
        onLongClick(e)
    }

    open fun onSingleClick(event: MotionEvent) {}
    open fun onDoubleClick(event: MotionEvent) {}
    open fun onScrollYClick(y: Float) {}
    open fun onScrollXClick(y: Float) {}
    open fun onLongClick(event: MotionEvent) {}
}


fun getAndroidId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

inline fun <reified T> String.toDataClass(): T {
    val gson = Gson()
    return gson.fromJson(this, T::class.java)
}


open class NoPaddingArrayAdapter<T>(context: Context, layoutId: Int, items: List<T>) :
    ArrayAdapter<T>(context, layoutId, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.setPadding(0, view.paddingTop, view.paddingRight, view.paddingBottom)
        (view as TextView).setTextColor(Color.WHITE)
        return view
    }
}

@SuppressLint("ClickableViewAccessibility")
class SpinnerNoSwipe : androidx.appcompat.widget.AppCompatSpinner {
    private var mGestureDetector: GestureDetector? = null

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setup()
    }

    private fun setup() {
        mGestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return performClick()
                }
            })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mGestureDetector!!.onTouchEvent(event)
        return true
    }
}

fun Activity.hideSystemBars() {
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}