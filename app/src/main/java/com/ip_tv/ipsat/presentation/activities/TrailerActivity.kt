package com.ip_tv.ipsat.presentation.activities
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.ip_tv.ipsat.databinding.ActivityTrailerBinding

class TrailerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrailerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding = ActivityTrailerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        class MyChrome : WebChromeClient() {
            private var mCustomView: View? = null
            private var mCustomViewCallback: CustomViewCallback? = null
            private var mOriginalSystemUiVisibility = 0

            override fun onHideCustomView() {
                (window.decorView as FrameLayout).removeView(
                    mCustomView
                )
                mCustomView = null
                window.decorView.systemUiVisibility =
                    mOriginalSystemUiVisibility
                mCustomViewCallback!!.onCustomViewHidden()
                mCustomViewCallback = null
            }

            override fun onShowCustomView(
                paramView: View,
                paramCustomViewCallback: CustomViewCallback
            ) {
                if (mCustomView != null) {
                    onHideCustomView()
                    return
                }
                mCustomView = paramView
                mOriginalSystemUiVisibility =
                    window.decorView.systemUiVisibility
                mCustomViewCallback = paramCustomViewCallback
                (window.decorView as FrameLayout).addView(
                    mCustomView,
                    FrameLayout.LayoutParams(-1, -1)
                )
                window.decorView.systemUiVisibility =
                    3846 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }

        val videoId = intent.getStringExtra("videoId") ?: ""
        binding.mediaInfoTrailer.apply {
            visibility = View.VISIBLE
            settings.javaScriptEnabled = true
            isSoundEffectsEnabled = true
            webChromeClient = MyChrome()
            loadUrl("https://www.youtube.com/embed/$videoId"!!)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }

}



