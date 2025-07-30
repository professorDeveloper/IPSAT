/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.activities

import Resource
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ActivitySplashBinding
import com.ip_tv.ipsat.domain.model.AppUpdate
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.presentation.dialogs.UpdateActivity
import com.ip_tv.ipsat.presentation.dialogs.UpdateNoticeBottomDialog
import com.ip_tv.ipsat.presentation.viewmodel.SplashViewModel
import com.ip_tv.ipsat.utils.DialogUtils
import com.ip_tv.ipsat.utils.alphaAnim
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.initActivity
import com.ip_tv.ipsat.utils.readData
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.locks.Lock
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val model by viewModels<SplashViewModel>()

    @Inject
    lateinit var userPreferenceManager: UserPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initActivity(this)
//        userPreferenceManager.isLogged = true
//        userPreferenceManager.subCode ="919907778996"

        model.isUpdateAvailableLiveData.observe(this) { available ->
            binding.checkProgress.gone()
            if (available) {
                model.getAppUpdateInfo()
            } else {
                startSplashFlow()
            }
        }

        model.getAppUpdateInfo.observe(this) { appUpdate ->
            showUpdateDialog(appUpdate)
        }
    }

    private fun showUpdateDialog(appUpdate: AppUpdate) {
        startActivity(
            UpdateActivity.newIntent(
                this,
                appUpdate
            )
        )
        finish()
    }

    private fun startSplashFlow() {
        lifecycleScope.launch {
            binding.appLogo.visible()
            binding.appLogo.alphaAnim()
            delay(2000)
            model.checkSubscribe()               // kicks off subscription check
            observeSubscription()
        }
    }

    private fun observeSubscription() {
        model.initSplash.observe(this) { state ->
            when (state) {
                is Resource.Loading -> binding.checkProgress.visible()
                is Resource.Error -> {
                    snackString(state.throwable.message ?: "Error")
                    binding.checkProgress.gone()
                    openLogin()
                }

                is Resource.Success -> {
                    binding.checkProgress.gone()
                    if (userPreferenceManager.isLocked) {
                        openLock()
                    } else {
                        openHome()
                    }
                }

                else -> {}
            }
        }

        model.isFirst.observe(this) {
            openLogin()
        }
    }

    private fun openLock() {
        ContextCompat.startActivity(
            this,
            Intent(this, LockActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK),
            null
        )
        finish()
    }

    private fun openHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun openDownloadsFolder() {
        startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }


}
