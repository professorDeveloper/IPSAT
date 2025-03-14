/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
        requireActivity().window.statusBarColor = requireActivity().getColor(R.color.status)
        setupUI()
        observeModel()
    }

    private fun observeModel() {
        val userPreferenceManager = UserPreferenceManager(requireContext())
//        profileViewModel.userDetail.observe(this) {
//            when (it) {
//                is Resource.Error -> {
//                    binding.shimmerCode.stopShimmer()
//                    binding.shimmerStatus.stopShimmer()
//                    binding.activatedDateShimmer.stopShimmer()
//                    binding.endDateShimmer.stopShimmer()
//                    binding.macAddressShimmer.stopShimmer()
//                    binding.tableLayout.visible()
//                    binding.shimmerTable.invisible()
//                    snackString("${it.throwable.message}")
//                }
//
//                is Resource.Loading -> {
//                    binding.tableLayout.invisible()
//                    binding.shimmerTable.visible()
//                    binding.shimmerCode.startShimmer()
//                    binding.shimmerStatus.startShimmer()
//                    binding.activatedDateShimmer.startShimmer()
//                    binding.endDateShimmer.startShimmer()
//                    binding.macAddressShimmer.startShimmer()
//                }
//
//                is Resource.Success -> {
//                    binding.tableLayout.visible()
//                    binding.shimmerTable.invisible()
//                    binding.subscriptionCodeTxt.text = userPreferenceManager.subCode
//                    binding.macAddressTxt.text = it.data.macAddress
//                    binding.subscriptionActivatedDateTxt.text =
//                        it.data.activatedAt.toString().toReadableDateTime()
//                    binding.subscriptionEndDateTxt.text =
//                        it.data.endDate.toString().toReadableDateTime()
//                    binding.subscriptionStatusTxt.text = it.data.status
//                    binding.shimmerCode.stopShimmer()
//                    binding.shimmerStatus.stopShimmer()
//                    binding.activatedDateShimmer.stopShimmer()
//                    binding.endDateShimmer.stopShimmer()
//                    binding.macAddressShimmer.stopShimmer()
//                }
//
//                else -> {}
//            }
//        }
    }

    private fun setupUI() {

        binding.subScriptionInfoContainer.setOnClickListener {
            findNavController().navigate(
                R.id.mySubscriptionPage,
                null,
                animationTransaction().build()
            )
        }
        binding.subscriptionCodeTxt.setOnClickListener {
            val clipboardManager =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val textToCopy = binding.subscriptionCodeTxt.text.toString()
            val clip = ClipData.newPlainText("Copied Text", textToCopy)
            clipboardManager.setPrimaryClip(clip)
        }
        binding.status.setOnClickListener {
            val clipboardManager =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val textToCopy = binding.status.text.toString()
            val clip = ClipData.newPlainText("Copied Text", textToCopy)
            clipboardManager.setPrimaryClip(clip)
        }

        binding.subscriptionSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
        binding.notificationContainer.setOnClickListener {
            findNavController().navigate(
                R.id.updateInfoScreen,
                null,
                animationTransaction().build()
            )
        }
    }
}