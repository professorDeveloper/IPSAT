package com.ip_tv.ipsat.presentation.activities

import Resource
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ActivitySplashBinding
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import com.ip_tv.ipsat.presentation.viewmodel.SplashViewModel
import com.ip_tv.ipsat.utils.alphaAnim
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.initActivity
import com.ip_tv.ipsat.utils.readData
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val model by viewModels<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initActivity(this@SplashActivity)
        manageDisplay()
        model.isLocked.observe(this@SplashActivity) {
            lifecycleScope.launch {
                binding.appLogo.visible()
                binding.appLogo.alphaAnim()
                delay(2000)
                model.checkSubscribe()
                observeModel()
            }

        }

    }

    private fun observeModel() {
        lifecycleScope.launch {
            model.initSplash.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { handleUserState(it) }
        }
        model.isFirst.observe(this@SplashActivity, openLoginObserver)
    }

    private fun manageDisplay() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private val openLoginObserver = Observer<Unit> {
        openLogin()
    }

    private fun handleUserState(state: Resource<SubscriptionResponse>) {
        when (state) {
            is Resource.Error -> {
                snackString("${state.throwable.message}")
                binding.checkProgress.gone()
                openLogin()
            }

            is Resource.Loading -> {
                binding.checkProgress.visible()
            }

            is Resource.Success -> {
                if (!CalcActivity.hasPermission) {
                    val pin: String = readData("app_password", this@SplashActivity) ?: ""
                    if (pin.isNotEmpty()) {
                        ContextCompat.startActivity(
                            this@SplashActivity,
                            Intent(this@SplashActivity, CalcActivity::class.java).putExtra(
                                    "code",
                                    pin
                                )
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK),
                            null
                        )
                        finish()
                        return
                    } else {
                        openHome()
                    }
                }
            }

            else -> {}
        }
    }

    private fun openHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}