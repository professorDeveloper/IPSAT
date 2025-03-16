/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ProfileScreenBinding
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.presentation.activities.SettingActivity
import com.ip_tv.ipsat.presentation.viewmodel.ProfileViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.invisible
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.toDateFromIso8601
import com.ip_tv.ipsat.utils.toReadableDateTime
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileScreen : BaseFragment<ProfileScreenBinding>(ProfileScreenBinding::inflate) {
    private val profileViewModel by viewModels<ProfileViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel.getUserDetail()
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor = requireActivity().getColor(R.color.colorPrimary)
        setupUI()
        observeModel()
    }

    private fun observeModel() {
        val userPreferenceManager = UserPreferenceManager(requireContext())
        profileViewModel.userDetail.observe(this) {
            when (it) {
                is Resource.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    snackString("${it.throwable.message}")
                }

                is Resource.Loading -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }

                is Resource.Success -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.subscriptionCodeTxt.text = userPreferenceManager.subCode
                    binding.macAddressTxt.text = it.data.macAddress
                    binding.subscriptionActivatedDateTxt.text =
                        it.data.activatedAt.toString().toReadableDateTime()
                    binding.subscriptionEndDateTxt.text =
                        it.data.endDate.toString().toReadableDateTime()
                    binding.subscriptionStatusTxt.text = it.data.status
                    val dateString = it.data.endDate.toString() // i added  test end date
                    val endDate = "2025-03-19T00:31:17.631Z".toDateFromIso8601()!!

                    val remainingTime = endDate.time - System.currentTimeMillis() + 10000
                    binding.countDownView.mediaCountdownText.text = "Subscription  will be end in"

                    object : CountDownTimer(remainingTime, 1000) {
                        @SuppressLint("SetTextI18n")
                        override fun onTick(millisUntilFinished: Long) {
                            val a = millisUntilFinished / 1000
                            binding.countDownView.mediaCountdown.text =
                                "${a / 86400} days ${a % 86400 / 3600} hrs ${a % 86400 % 3600 / 60} mins ${a % 86400 % 3600 % 60} secs"
                        }

                        override fun onFinish() {
//                            binding.countDownView.mediaCountdownContainer.visibility = View.GONE
                        }
                    }.start()


                }

                else -> {}
            }
        }
    }

    private fun setupUI() {

        binding.swipeRefreshLayout.setOnRefreshListener {
            profileViewModel.getUserDetail()
        }
        binding.subscriptionCodeTxt.setOnClickListener {
            val clipboardManager =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val textToCopy = binding.subscriptionCodeTxt.text.toString()
            val clip = ClipData.newPlainText("Copied Text", textToCopy)
            clipboardManager.setPrimaryClip(clip)
        }
        binding.subscriptionStatusTxt.setOnClickListener {
            val clipboardManager =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val textToCopy = binding.subscriptionStatusTxt.text.toString()
            val clip = ClipData.newPlainText("Copied Text", textToCopy)
            clipboardManager.setPrimaryClip(clip)
        }

        binding.subscriptionSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
        binding.subscriptionUpdateInfo.setOnClickListener {
            findNavController().navigate(
                R.id.updateInfoScreen,
                null,
                animationTransaction().build()
            )
        }
    }
}