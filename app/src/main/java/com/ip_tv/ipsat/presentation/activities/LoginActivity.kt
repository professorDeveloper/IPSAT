package com.ip_tv.ipsat.presentation.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ActivityLoginBinding
import com.ip_tv.ipsat.presentation.viewmodel.LoginViewModel
import com.ip_tv.ipsat.utils.AuthState
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.visible
import com.zbekz.tashkentmetro.utils.getMacAddress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginModel by viewModels<LoginViewModel>()
    private lateinit var binding: ActivityLoginBinding
    private val PERMISSIONS_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupPhoneDisplay()
        manageButtons()
        observeModel()
    }
    private fun observeModel(){
        lifecycleScope.launch {
            loginModel.loginState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { handleAuthState(it) }
        }

    }

    private fun handleAuthState(state: AuthState) {
        when (state) {
            is AuthState.Error -> {
                Log.d("GG", "handleAuthState:${state.message} ")
                binding.authProgress.gone()
                binding.invalidCodeContainer.visible()
                binding.errorTxt.text= state.message
                binding.activateBtn.visible()
            }

            AuthState.Loading -> {
                binding.activateBtn.gone()
                binding.invalidCodeContainer.gone()
                Log.d("GG", "handleAuthState:LOADING ")
                binding.authProgress.visible()
            }
            is AuthState.Verified -> {
                Log.d("GG", "handleAuthState:VERIFED ")
                binding.invalidCodeContainer.gone()
                binding.authProgress.gone()
                binding.activateBtn.visible()
                snackString(state.response.message)
            }

            else ->Unit
        }
    }

    private fun manageButtons(){
        binding.subscriptionCodeTxt.addTextChangedListener {
            binding.activateBtn.isEnabled = it.toString().isNotEmpty()
        }
        binding.activateBtn.setOnClickListener {
            val macAddress = getMacAddress(this)
            println("MAC Address: $macAddress")
            loginModel.activateLogin(code = binding.subscriptionCodeTxt.text.toString(), macAddress = macAddress?:"")
        }
    }
    private fun  setupPhoneDisplay(){
        ViewCompat.setOnApplyWindowInsetsListener(this@LoginActivity.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, you can fetch MAC address here
                val macAddress = getMacAddress(this)
                println("MAC Address: $macAddress")
            } else {
                println("Permissions denied. Cannot fetch MAC address.")
            }
        }
    }
}
