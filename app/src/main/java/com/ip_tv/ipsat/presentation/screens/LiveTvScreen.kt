package com.ip_tv.ipsat.presentation.screens

import Resource
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.LiveTvScreenBinding
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.presentation.adapters.TabAdapter
import com.ip_tv.ipsat.presentation.viewmodel.LiveTvScreenViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LiveTvScreen : BaseFragment<LiveTvScreenBinding>(LiveTvScreenBinding::inflate) {

    private val model by viewModels<LiveTvScreenViewModel>()
    private var isBannerLoaded = false
    override fun onViewCreate(savedInstanceState: Bundle?) {
        requireActivity().window.statusBarColor = requireActivity().getColor(R.color.colorPrimary)
        if (!isBannerLoaded) {
            model.loadCategory()
        }
        observeCategory()
    }

    private fun observeCategory() {
        if (isBannerLoaded) return
        lifecycleScope.launch {
            model.tvCategory.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                when (it) {
                    is Resource.Success -> {
                        binding.mainViewPager2.visible()
                        binding.tabLayout.visible()
                        binding.progressBar.gone()
                        if (binding.mainViewPager2.adapter == null) {
                            binding.mainViewPager2.adapter = TabAdapter(it.data, requireActivity())
                            binding.mainViewPager2.isUserInputEnabled=false
                            TabLayoutMediator(binding.tabLayout, binding.mainViewPager2) { _, _ ->
                            }.attach()
                            setTab(it.data)

                        }
                        isBannerLoaded = true
                    }
                    is Resource.Error -> {
                        showSnack(binding.root, it.throwable.message.toString())
                    }

                    is Resource.Loading -> {
                        binding.mainViewPager2.gone()
                        binding.tabLayout.gone()
                        binding.progressBar.visible()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setTab(channelCategory: ChannelCategory) {
        binding.apply {
            val tabCount = tabLayout.tabCount
            for (i in 0 until tabCount) {
                binding.tabLayout.getTabAt(i)?.text = channelCategory[i].name
            }

        }
    }


}