package com.ip_tv.ipsat.presentation.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.DefaultTimeBar
import com.ip_tv.ipsat.R

class PlayerActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
    }
    @SuppressLint("ViewConstructor", "UnsafeOptInUsageError")
    class ExtendedTimeBar(
        context: Context,
        attrs: AttributeSet?
    ) : DefaultTimeBar(context, attrs) {
        private var enabled = false
        private var forceDisabled = false
        override fun setEnabled(enabled: Boolean) {
            this.enabled = enabled
            super.setEnabled(!forceDisabled && this.enabled)
        }

        fun setForceDisabled(forceDisabled: Boolean) {
            this.forceDisabled = forceDisabled
            isEnabled = enabled
        }
    }
}
