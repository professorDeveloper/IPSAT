package com.ip_tv.ipsat.presentation.screens

import Resource
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.addCallback
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ferfalk.simplesearchview.SimpleSearchView
import com.ferfalk.simplesearchview.utils.DimensUtils.convertDpToPx
import com.google.android.material.tabs.TabLayoutMediator
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.LiveTvScreenBinding
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.presentation.adapters.TabAdapter
import com.ip_tv.ipsat.presentation.viewmodel.LiveTvScreenViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.LocalData.EXTRA_REVEAL_CENTER_PADDING
import com.ip_tv.ipsat.utils.LocalData.changeSearchResponse
import com.ip_tv.ipsat.utils.LocalData.clearSearchResponse
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
//        val window = requireActivity().window
//        WindowCompat.setDecorFitsSystemWindows(window, true)

        if (!isBannerLoaded) {
            model.loadCategory()
            model.loadEventChannels()
        }
        observeCategory()

    }

    private fun setupSearchView(menu: Menu) = with(binding) {
        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)

        searchView.setTabLayout(tabLayout)
        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    clearSearchResponse.invoke()
                } else {
                    changeSearchResponse(newText)

                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    changeSearchResponse(query)
                } else {
                    clearSearchResponse.invoke()
                }
                return true
            }

            override fun onQueryTextCleared(): Boolean {
                binding.searchView.closeSearch(true)
                clearSearchResponse.invoke()
                return true
            }
        })
        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                clearSearchResponse.invoke()
            }

            override fun onSearchViewClosedAnimation() {
            }

            override fun onSearchViewShown() {
            }

            override fun onSearchViewShownAnimation() {
            }

        })
        val revealCenter = searchView.revealAnimationCenter
        revealCenter!!.x -= convertDpToPx(EXTRA_REVEAL_CENTER_PADDING, requireActivity())
    }

    private fun observeCategory() {
        if (isBannerLoaded) return
        lifecycleScope.launch {
            model.tvCategory.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                when (it) {
                    is Resource.Success -> {
                        model.eventChannelsData.observe(viewLifecycleOwner) { dataEvent ->
                            when (dataEvent) {
                                is Resource.Success -> {
                                    binding.mainViewPager2.visible()
                                    LocalData.setDataHaveListener {
                                        val menuItem =
                                            binding.toolbar.menu.findItem(R.id.action_search)
                                        menuItem.isVisible = it
                                    }
                                    binding.tabLayout.visible()
                                    setupSearchView(binding.toolbar.menu)
                                    binding.progressBar.gone()
                                    if (binding.mainViewPager2.adapter == null) {
                                        binding.mainViewPager2.adapter =
                                            TabAdapter(
                                                it.data,
                                                it.data,
                                                eventList = dataEvent.data, requireActivity()
                                            )
                                        TabLayoutMediator(
                                            binding.tabLayout,
                                            binding.mainViewPager2
                                        ) { _, _ ->
                                        }.attach()
                                        setTab(it.data)
                                        binding.mainViewPager2.isUserInputEnabled = false
                                    }
                                    isBannerLoaded = true
                                }

                                else -> {}
                            }
                        }
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