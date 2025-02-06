package com.ip_tv.ipsat.presentation.screens

import android.os.Bundle
import com.ip_tv.ipsat.databinding.ChannelCategoryScreenBinding
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
import com.ip_tv.ipsat.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChannelCategoryScreen :
    BaseFragment<ChannelCategoryScreenBinding>(ChannelCategoryScreenBinding::inflate) {
    private lateinit var bundleData: ChannelCategoryItem
    override fun onViewCreate(savedInstanceState: Bundle?) {
        loadParsedData()

    }


    private fun loadParsedData() {
        bundleData = requireArguments().getSerializable("data") as ChannelCategoryItem
    }


}