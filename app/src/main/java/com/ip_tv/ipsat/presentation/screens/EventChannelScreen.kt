package com.ip_tv.ipsat.presentation.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.data.local.mapper.toChannelResponseItem
import com.ip_tv.ipsat.databinding.ChannelCategoryScreenBinding
import com.ip_tv.ipsat.databinding.EventChannelScreenBinding
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.domain.model.EventModelItem
import com.ip_tv.ipsat.presentation.activities.LiveTvActivity
import com.ip_tv.ipsat.presentation.adapters.CategoryAdapter
import com.ip_tv.ipsat.presentation.adapters.ChannelAdapter
import com.ip_tv.ipsat.presentation.adapters.EventChannelAdapter
import com.ip_tv.ipsat.presentation.viewmodel.LiveTvScreenViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.DialogUtils
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.filterByKeywords
import com.ip_tv.ipsat.utils.filterChannelsByCategory
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.toast
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

@AndroidEntryPoint
class EventChannelScreen :
    BaseFragment<EventChannelScreenBinding>(EventChannelScreenBinding::inflate) {

    private lateinit var bundleData: ChannelCategoryItem
    private val model by viewModels<LiveTvScreenViewModel>()
    private var isBannerLoaded = false
    private var categoryList = ArrayList<ChannelCategoryItem>()
    private val channelResponseList = ArrayList<EventModelItem>()

    private val channelAdapter by lazy { EventChannelAdapter() }

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreate(savedInstanceState: Bundle?) {
        loadParsedData()
        categoryAdapter = CategoryAdapter()
        observeModel()
        observeChannelData()
    }

    private fun loadParsedData() {
        arguments?.getSerializable("listData")?.let {
            categoryList = it as ChannelCategory
        } ?: run {
            Log.e("ChannelCategoryScreen", "Error: bundleData is null")
            return
        }
        arguments?.getSerializable("data")?.let {
            bundleData = it as ChannelCategoryItem
        } ?: run {
            Log.e("ChannelCategoryScreen", "Error: bundleData is null")
            return
        }


    }

    private fun setupAdapters() {
        binding.channelRv.adapter = channelAdapter
        binding.subCategoryRv.adapter = categoryAdapter
        channelAdapter.setChannelItemClickListener {
            LiveTvActivity.currentCategory = bundleData
            LiveTvActivity.categoryList = categoryList
            LiveTvActivity.eventList = LocalData.eventList
            val intent = LiveTvActivity.newIntent(requireActivity(), it.toChannelResponseItem())
            requireActivity().startActivity(
                intent
            )
        }
    }

    private fun observeModel() {
        if (isBannerLoaded) return
        lifecycleScope.launch {
            model.subCategoryData.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.progressBar.gone()
                            binding.subCategoryRv.visible()
                            categoryAdapter.submitList(
                                resource.data
                            )

                        }

                        is Resource.Error -> {
                            binding.progressBar.gone()
                            Log.e(
                                "ChannelCategoryScreen",
                                "Error loading subcategories: ${resource.throwable.message}"
                            )
                        }

                        is Resource.Loading -> {
                            binding.progressBar.visible()
                            binding.subCategoryRv.gone()
                            binding.channelRv.gone()
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun observeChannelData() {
        if (isBannerLoaded) return
        binding.progressBar.gone()
        binding.channelRv.visible()
        LocalData.isDataHave.invoke(LocalData.eventList.filter { it.category == bundleData.id }
            .isNotEmpty())

        LocalData.setSearchResponseListener {
            search(it)
        }

        LocalData.setClearSearchResponseListener {
            if (LocalData.selectedCategory.isNotEmpty()) {
                val filteredList = LocalData.eventList
                channelAdapter.submitList(filteredList.filter { it.category == bundleData.id })
            } else {
                channelAdapter.submitList(LocalData.eventList.filter { it.category == bundleData.id })
            }
        }

        if (LocalData.eventList.isNotEmpty()) {
            binding.placeHolder.gone()
            channelResponseList.clear()
            channelResponseList.addAll(LocalData.eventList.filter { it.category == bundleData.id })
            channelAdapter.submitList(LocalData.eventList.filter { it.category == bundleData.id })
            categoryAdapter.setItemCLickListener { selectedItems ->
                LocalData.selectedCategory.clear()
                LocalData.selectedCategory.addAll(selectedItems)
                val filteredList = LocalData.eventList.filter { it.category == bundleData.id }
                if (selectedItems.isNotEmpty() && filteredList.isNotEmpty()) {
                    channelAdapter.submitList(
                        filteredList
                    )
                } else if (selectedItems.isEmpty()) {
                    channelAdapter.submitList(LocalData.eventList.filter { it.category == bundleData.id })
                }
            }
            setupAdapters()
            isBannerLoaded = true
        } else {
            binding.placeHolder.visible()
            binding.subCategoryRv.gone()
            binding.channelRv.gone()
        }
        Log.d("ChannelCategoryScreen", "Channels loaded successfully")
    }

    fun search(query: String) {
        if (query.isNotEmpty()) {
            channelAdapter.query(query)

        }
    }
}