package com.ip_tv.ipsat.presentation.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.UpdateInfoScreenBinding
import com.ip_tv.ipsat.presentation.adapters.UpdateAdapter
import com.ip_tv.ipsat.presentation.viewmodel.ProfileViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.initActivity
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateInfoScreen : BaseFragment<UpdateInfoScreenBinding>(UpdateInfoScreenBinding::inflate) {
    private val model by viewModels<ProfileViewModel>()
    private val adapter = UpdateAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.getNotificationList()
        initActivity(requireActivity())
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        observeModel()
        loadRefresh()
    }

    private fun observeModel () {
        model.notificationList.observe(this@UpdateInfoScreen) {
            when (it) {
                is Resource.Error -> {
                    binding.updateRefresh.isRefreshing = false
                    showSnack(binding.root, it.throwable.message.toString())
                }

                is Resource.Loading -> {
                    binding.updateRefresh.isRefreshing = true
                }
                is Resource.Success -> {
                    binding.updateRefresh.isRefreshing = false
                    binding.rvUpdateInfo.adapter = adapter
                    adapter.submitList(it.data.messages)
                }
                else -> {}
            }
        }
    }
    private fun loadRefresh() {
        binding.updateRefresh.setSlingshotDistance(128)
        binding.updateRefresh.setProgressViewEndTarget(false, 128)
        binding.updateRefresh.setOnRefreshListener {
            model.getNotificationList()
        }
    }

}