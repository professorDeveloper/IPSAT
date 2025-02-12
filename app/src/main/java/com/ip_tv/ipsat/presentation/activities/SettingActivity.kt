package com.ip_tv.ipsat.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.DialogSetPasswordBinding
import com.ip_tv.ipsat.databinding.DialogUserAgentBinding
import com.ip_tv.ipsat.databinding.SettingScreenBinding
import com.ip_tv.ipsat.presentation.viewmodel.SettingViewModel
import com.ip_tv.ipsat.utils.BiometricPromptUtils
import com.ip_tv.ipsat.utils.customAlertDialog
import com.ip_tv.ipsat.utils.initActivity
import com.ip_tv.ipsat.utils.openLinkInBrowser
import com.ip_tv.ipsat.utils.pop
import com.ip_tv.ipsat.utils.readData
import com.ip_tv.ipsat.utils.saveData
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.startMainActivity
import com.ip_tv.ipsat.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private val binding by lazy { SettingScreenBinding.inflate(layoutInflater) }
    private val model by viewModels<SettingViewModel>()
    private val restartMainActivity = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = startMainActivity(this@SettingActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, restartMainActivity)
        initClick()
        observeModel()
    }

    private fun observeModel() {
        lifecycleScope.launch {
            model.currentTheme.observe(this@SettingActivity) {
                when (it) {
                    0 -> {
                        binding.settingsUiLight.alpha = 0.33f
                        binding.settingsUiDark.alpha = 0.33f
                        binding.settingsUiAuto.alpha = 1f
                    }

                    1 -> {
                        binding.settingsUiLight.alpha = 1f
                        binding.settingsUiAuto.alpha = 0.33f
                        binding.settingsUiDark.alpha = 0.33f
                    }

                    2 -> {
                        binding.settingsUiDark.alpha = 1f
                        binding.settingsUiAuto.alpha = 0.33f
                        binding.settingsUiLight.alpha = 0.33f
                    }
                }
            }
        }
    }


    private fun initClick() {

        val data = readData<String>(fileName = "app_password", context = this@SettingActivity) ?: ""
        if (data.isNotEmpty()) {
            binding.appLock.isChecked = true
            binding.appLock.text = "Change Passcode"
        } else {
            binding.appLock.isChecked = false
            binding.appLock.text = "Add Passcode"
        }

        binding.appLock.setOnClickListener {
            customAlertDialog().apply {
                val view = DialogSetPasswordBinding.inflate(layoutInflater)
                setTitle(R.string.app_lock)
                setCustomView(view.root)
                setPosButton(R.string.ok) { ->
                    if (view.forgotPasswordCheckbox.isChecked) {
                        saveData("app_forgot", true)
                    }
                    val password = view.passwordInput.text.toString()
                    val confirmPassword = view.confirmPasswordInput.text.toString()
                    if (password == confirmPassword && password.isNotEmpty()) {
                        saveData("app_password", password)
                        if (view.biometricCheckbox.isChecked) {
                            val canBiometricPrompt = BiometricManager.from(applicationContext)
                                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

                            if (canBiometricPrompt) {
                                val biometricPrompt =
                                    BiometricPromptUtils.createBiometricPrompt(this@SettingActivity) { _ ->
                                        val token = UUID.randomUUID().toString()
                                        saveData(
                                            "biometric_pass", token
                                        )

                                        val checkData = readData<String>(
                                            fileName = "app_password",
                                            context = this@SettingActivity
                                        ) ?: ""
                                        if (checkData.isNotEmpty()) {
                                            binding.appLock.isChecked = true
                                            binding.appLock.text = "Change Passcode"
                                        } else {
                                            binding.appLock.isChecked = false
                                            binding.appLock.text = "Add Passcode"
                                        }
                                        toast("Success")
                                    }
                                val promptInfo =
                                    BiometricPromptUtils.createPromptInfo(this@SettingActivity)
                                biometricPrompt.authenticate(promptInfo)
                            }
                        } else {

                            val checkData = readData<String>(
                                fileName = "app_password",
                                context = this@SettingActivity
                            ) ?: ""
                            if (checkData.isNotEmpty()) {
                                binding.appLock.isChecked = true
                                binding.appLock.text = "Change Passcode"
                            } else {
                                binding.appLock.isChecked = false
                                binding.appLock.text = "Add Passcode"
                            }
                            saveData("biometric_pass", "")
                            toast("Success")
                        }
                    } else {
                        val checkData = readData<String>(
                            fileName = "app_password",
                            context = this@SettingActivity
                        ) ?: ""
                        if (checkData.isNotEmpty()) {
                            binding.appLock.isChecked = true
                            binding.appLock.text = "Change Passcode"
                        } else {
                            binding.appLock.isChecked = false
                            binding.appLock.text = "Add Passcode"
                        }
                        toast(getString(R.string.password_mismatch))
                    }
                }
                setNegButton(R.string.cancel) {

                    val checkData =
                        readData<String>(fileName = "app_password", context = this@SettingActivity)
                            ?: ""
                    if (checkData.isNotEmpty()) {
                        binding.appLock.isChecked = true
                        binding.appLock.text = "Change Passcode"
                    } else {
                        binding.appLock.isChecked = false
                        binding.appLock.text = "Add Passcode"
                    }
                }
                setNeutralButton(R.string.remove) { ->
                    saveData("app_password", "")
                    saveData("biometric_pass", "")
                    saveData("app_forgot", false)
                    toast("Success")
                    binding.appLock.isChecked = false
                }
                setOnShowListener {
                    view.passwordInput.requestFocus()
                    val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate(
                        BiometricManager.Authenticators.BIOMETRIC_WEAK,
                    ) == BiometricManager.BIOMETRIC_SUCCESS
                    view.biometricCheckbox.isVisible = canAuthenticate
                    view.biometricCheckbox.isChecked =
                        (readData("biometric_pass", this@SettingActivity) ?: "").isNotEmpty()
                    view.forgotPasswordCheckbox.isChecked =
                        readData("app_forgot", this@SettingActivity) ?: false
                }
                show()
            }

        }
        binding.settingsUiAuto.setOnClickListener {
            saveData("current_theme", 0)
            finish()
            startActivity(Intent(this, SettingActivity::class.java))
            initActivity(this)
        }
        binding.settingsUiLight.setOnClickListener {
            saveData("current_theme", 1)
            finish()
            startActivity(Intent(this, SettingActivity::class.java))
            initActivity(this)
        }
        binding.settingsUiDark.setOnClickListener {
            saveData("current_theme", 2)
            finish()
            startActivity(Intent(this, SettingActivity::class.java))
            initActivity(this)
        }

    }


}