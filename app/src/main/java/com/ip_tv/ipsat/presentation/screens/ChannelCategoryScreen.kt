package com.ip_tv.ipsat.presentation.screens

import Resource
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ip_tv.ipsat.databinding.ChannelCategoryScreenBinding
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.domain.model.SubCategoryItem
import com.ip_tv.ipsat.presentation.activities.LiveTvActivity
import com.ip_tv.ipsat.presentation.adapters.CategoryAdapter
import com.ip_tv.ipsat.presentation.adapters.ChannelAdapter
import com.ip_tv.ipsat.presentation.viewmodel.LiveTvScreenViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.LocalData.isDataHave
import com.ip_tv.ipsat.utils.LocalData.selectedCategory
import com.ip_tv.ipsat.utils.LocalData.setClearSearchResponseListener
import com.ip_tv.ipsat.utils.LocalData.setSearchResponseListener
import com.ip_tv.ipsat.utils.filterByKeywords
import com.ip_tv.ipsat.utils.filterChannelsByCategory
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelCategoryScreen :
    BaseFragment<ChannelCategoryScreenBinding>(ChannelCategoryScreenBinding::inflate) {

    private lateinit var bundleData: ChannelCategoryItem
    private val model by viewModels<LiveTvScreenViewModel>()
    private var isBannerLoaded = false
    private var categoryList = ArrayList<ChannelCategoryItem>()
    private val channelResponseList = ArrayList<ChannelResponseItem>()
    private val channelAdapter by lazy { ChannelAdapter() }

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreate(savedInstanceState: Bundle?) {
        loadParsedData()

        if (!isBannerLoaded) {
            model.loadChannelsByCategory(bundleData.id)
            model.loadSubCategory()
        }
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
            val intent = LiveTvActivity.newIntent(requireActivity(), it)
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
                                resource.data.filterChannelsByCategory(
                                    bundleData.id
                                )
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
        model.channelsData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.progressBar.gone()
                    binding.channelRv.visible()
                    isDataHave.invoke(resource.data.isNotEmpty())

                    setSearchResponseListener {
                        search(it)
                    }

                    setClearSearchResponseListener {
                        if (selectedCategory.isNotEmpty()) {
                            val filteredList = resource.data.filterByKeywords(selectedCategory)
                            channelAdapter.submitList(filteredList)
                        } else {
                            channelAdapter.submitList(resource.data)
                        }
                    }

                    if (resource.data.isNotEmpty()) {
                        binding.placeHolder.gone()
                        channelResponseList.clear()
                        channelResponseList.addAll(resource.data)
                        channelAdapter.submitList(resource.data)
                        categoryAdapter.setItemCLickListener { selectedItems ->
                            selectedCategory.clear()
                            selectedCategory.addAll(selectedItems)
                            val filteredList = resource.data.filterByKeywords(selectedItems)
                            if (selectedItems.isNotEmpty() && filteredList.isNotEmpty()) {
                                channelAdapter.submitList(
                                    filteredList
                                )
                            } else if (selectedItems.isEmpty()) {
                                channelAdapter.submitList(resource.data)
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

                is Resource.Error -> {
                    binding.progressBar.gone()
                    Log.e(
                        "ChannelCategoryScreen",
                        "Error loading channels: ${resource.throwable.message}"
                    )
                }

                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.channelRv.gone()
                }

                else -> {}
            }
        }
    }

    fun search(query: String) {
        if (query.isNotEmpty()) {
            channelAdapter.query(query)

        }
    }
}
