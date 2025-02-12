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
        requireActivity().window.statusBarColor = Color.parseColor("#E5F4FA")
        setupUI()
        observeModel()
    }

    private fun observeModel() {
        val userPreferenceManager = UserPreferenceManager(requireContext())
        profileViewModel.userDetail.observe(this) {
            when (it) {
                is Resource.Error -> {
                    binding.shimmerCode.stopShimmer()
                    binding.shimmerStatus.stopShimmer()
                    binding.activatedDateShimmer.stopShimmer()
                    binding.endDateShimmer.stopShimmer()
                    binding.macAddressShimmer.stopShimmer()
                    binding.tableLayout.visible()
                    binding.shimmerTable.gone()
                    snackString("${it.throwable.message}")
                }

                is Resource.Loading -> {
                    binding.tableLayout.gone()
                    binding.shimmerTable.visible()
                    binding.shimmerCode.startShimmer()
                    binding.shimmerStatus.startShimmer()
                    binding.activatedDateShimmer.startShimmer()
                    binding.endDateShimmer.startShimmer()
                    binding.macAddressShimmer.startShimmer()
                }

                is Resource.Success -> {
                    binding.tableLayout.visible()
                    binding.shimmerTable.gone()
                    binding.subscriptionCodeTxt.text = userPreferenceManager.subCode
                    binding.macAddressTxt.text = it.data.macAddress
                    binding.subscriptionActivatedDateTxt.text =
                        it.data.activatedAt.toString().toReadableDateTime()
                    binding.subscriptionEndDateTxt.text =
                        it.data.endDate.toString().toReadableDateTime()
                    binding.subscriptionStatusTxt.text = it.data.status
                    binding.shimmerCode.stopShimmer()
                    binding.shimmerStatus.stopShimmer()
                    binding.activatedDateShimmer.stopShimmer()
                    binding.endDateShimmer.stopShimmer()
                    binding.macAddressShimmer.stopShimmer()
                }

                else -> {}
            }
        }
    }

    private fun setupUI() {

        binding.subscriptionBtn.setOnClickListener {
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