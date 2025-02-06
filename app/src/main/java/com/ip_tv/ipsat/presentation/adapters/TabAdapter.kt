package com.ip_tv.ipsat.presentation.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
import com.ip_tv.ipsat.presentation.screens.ChannelCategoryScreen

class TabAdapter(private var categories: List<ChannelCategoryItem>, fragmentManager: FragmentActivity) :
    FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return categories.size
    }

    override fun createFragment(position: Int): Fragment {
        val item = ChannelCategoryScreen()
        val bundle = Bundle()
        bundle.putSerializable("data", categories[position])
        item.arguments = bundle
        return item
    }


}