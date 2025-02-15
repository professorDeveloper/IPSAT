package com.ip_tv.ipsat.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.PopupWindow
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
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
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
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private val binding by lazy { SettingScreenBinding.inflate(layoutInflater) }
    private val model by viewModels<SettingViewModel>()

    @Inject
     lateinit var userPreferenceManager: UserPreferenceManager
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


        binding.appLock.setOnClickListener {
            if ((readData("app_password", this@SettingActivity) ?: "").isNotEmpty()) {
                showPopupMenu(binding.appLock)
            } else {
                showEditDialog()
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

    private fun showEditDialog() {
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
                if (password == confirmPassword && password.isNotEmpty() && password.length == 4) {
                    saveData("app_password", password)
                    userPreferenceManager.isLocked = true
                    saveData("biometric_pass", "")

                    toast("Success")
                } else {

                    toast(getString(R.string.password_mismatch))
                }
            }
            setNegButton(R.string.cancel) {
                toast("Action cancelled")
            }
            setOnShowListener {
                view.passwordInput.requestFocus()
                view.forgotPasswordCheckbox.isChecked =
                    readData("app_forgot", this@SettingActivity) ?: false
            }
            show()
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun showPopupMenu(anchor: View) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.menu_custom, null)

        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val tvEdit: LinearLayout = view.findViewById(R.id.tv_change_passcode)
        val clearPasscode: LinearLayout = view.findViewById(R.id.remove_passcode)


        tvEdit.requestFocus()

        tvEdit.setOnClickListener {
            popupWindow.dismiss()
            showEditDialog()
        }


        clearPasscode.setOnClickListener {
            saveData("app_password", "")
            saveData("app_forgot", false)
            userPreferenceManager.isLocked = false
            popupWindow.dismiss()
            finish()
            startActivity(Intent(this, SettingActivity::class.java))
            initActivity(this)
            toast("Passcode removed")
        }

        popupWindow.elevation = 10f
        popupWindow.showAsDropDown(anchor, 0, 0)
    }


}