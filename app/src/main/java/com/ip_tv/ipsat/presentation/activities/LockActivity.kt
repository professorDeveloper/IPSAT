package com.ip_tv.ipsat.presentation.activities

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ActivityLockBinding
import com.ip_tv.ipsat.utils.readData

class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockBinding
    private val enteredPin = StringBuilder()
    private val pinLength = 4
    private val correctPin = readData<String>("app_password")?:"1234" // To‘g‘ri PIN shu yerda belgilangan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pinDots = listOf(
            binding.dot1, binding.dot2, binding.dot3, binding.dot4
        )

        val buttons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6, binding.button7,
            binding.button8, binding.button9
        )

        val buttonAnim = AnimationUtils.loadAnimation(this, R.anim.button_scale)

        for (button in buttons) {
            button.setOnClickListener {
                button.startAnimation(buttonAnim)
                if (enteredPin.length < pinLength) {
                    enteredPin.append((it as Button).text)
                    updatePinDots(pinDots)

                    if (enteredPin.length == pinLength) {
                        onPinEntered(enteredPin.toString())
                    }
                }
            }
        }

        binding.buttonBackspace.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin.deleteCharAt(enteredPin.length - 1)
                updatePinDots(pinDots)
            }
        }

        binding.buttonClear.setOnClickListener {
            enteredPin.clear()
            updatePinDots(pinDots)
        }
    }

    fun onPinEntered(pin: String) {
        if (pin == correctPin) {
            binding.tvPinPrompt.text = "PIN KIRITILDI ✅"
            binding.tvPinPrompt.setTextColor(resources.getColor(R.color.map_green, null))
        } else {
            binding.tvPinPrompt.text = "Noto‘g‘ri PIN ❌"
            binding.tvPinPrompt.setTextColor(resources.getColor(R.color.map_red, null))

            // Shake animation
            val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)
            binding.buttonContainer.startAnimation(shakeAnim)

            enteredPin.clear()
            updatePinDots(listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4))
        }
    }

    private fun updatePinDots(pinDots: List<View>) {

        for (i in pinDots.indices) {
            if (i < enteredPin.length) {
                pinDots[i].setBackgroundResource(R.drawable.dot_selected)
            } else {
                pinDots[i].setBackgroundResource(R.drawable.dot_unselected)
            }
        }
    }
}
