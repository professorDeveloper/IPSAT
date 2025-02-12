package com.ip_tv.ipsat.presentation.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ActivityCalcBinding
import com.ip_tv.ipsat.utils.BiometricPromptUtils
import com.ip_tv.ipsat.utils.CalcStack
import com.ip_tv.ipsat.utils.NumberConverter.Companion.toBinary
import com.ip_tv.ipsat.utils.NumberConverter.Companion.toHex
import com.ip_tv.ipsat.utils.initActivity
import com.ip_tv.ipsat.utils.readData
import com.ip_tv.ipsat.utils.startMainActivity

class CalcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalcBinding
    private lateinit var code: String
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        success()
    }
    private val stack = CalcStack()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity(this)
        binding = ActivityCalcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.doOnAttach {
            initActivity(this)
        }
        code = intent.getStringExtra("code") ?: "0"

        binding.apply {
            button0.setOnClickListener { stack.add('0'); updateDisplay() }
            button1.setOnClickListener { stack.add('1'); updateDisplay() }
            button2.setOnClickListener { stack.add('2'); updateDisplay() }
            button3.setOnClickListener { stack.add('3'); updateDisplay() }
            button4.setOnClickListener { stack.add('4'); updateDisplay() }
            button5.setOnClickListener { stack.add('5'); updateDisplay() }
            button6.setOnClickListener { stack.add('6'); updateDisplay() }
            button7.setOnClickListener { stack.add('7'); updateDisplay() }
            button8.setOnClickListener { stack.add('8'); updateDisplay() }
            button9.setOnClickListener { stack.add('9'); updateDisplay() }
            buttonDot.setOnClickListener { stack.add('.'); updateDisplay() }
            buttonAdd.setOnClickListener { stack.add('+'); updateDisplay() }
            buttonSubtract.setOnClickListener { stack.add('-'); updateDisplay() }
            buttonMultiply.setOnClickListener { stack.add('*'); updateDisplay() }
            buttonDivide.setOnClickListener { stack.add('/'); updateDisplay() }
            buttonEquals.setOnClickListener {
                try {
                    val ans = stack.evaluate()
                    updateDisplay()
                    binding.displayBinary.text = ans.toBinary()
                    binding.displayHex.text = ans.toHex()
                } catch (e: Exception) {
                    display.text = getString(R.string.error)
                }
            }
            buttonClear.setOnClickListener {
                stack.clear()
                binding.displayBinary.text = ""
                binding.displayHex.text = ""
                binding.display.text = "0"
            }
            if (readData<Boolean>("app_forgot", this@CalcActivity, false) == true) {
                buttonClear.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            handler.postDelayed(runnable, 10000)
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            v.performClick()
                            handler.removeCallbacks(runnable)
                            true
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            handler.removeCallbacks(runnable)
                            true
                        }

                        else -> false
                    }
                }
            }
            buttonBackspace.setOnClickListener {
                stack.remove()
                updateDisplay()
            }
            display.text = "0"
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasPermission) {
            success()
        }
        if ((readData<String>("biometric_pass", this) ?: "").isNotEmpty()) {
            val bioMetricPrompt = BiometricPromptUtils.createBiometricPrompt(this) {
                success()
            }
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            bioMetricPrompt.authenticate(promptInfo)
        }
    }

    private fun success() {
        hasPermission = true
        startMainActivity(this)
    }

    private fun updateDisplay() {
        if (stack.getExpression().isEmpty()) {
            binding.display.text = "0"
            return
        }
        val expression = stack.getExpression().replace("*", "×").replace("/", "÷")
        val spannable = SpannableString(expression)

        binding.display.text = spannable
        val text = binding.display.text.toString()
        if (text == code) {
            success()
        }
    }

    companion object {
        var hasPermission = false
    }
}