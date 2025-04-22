package com.ip_tv.ipsat.presentation.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
import com.ip_tv.ipsat.domain.model.EventModel
import com.ip_tv.ipsat.domain.model.EventModelItem
import com.ip_tv.ipsat.presentation.screens.ChannelCategoryScreen
import com.ip_tv.ipsat.presentation.screens.EventChannelScreen
import com.ip_tv.ipsat.presentation.screens.LiveTvScreen
import com.ip_tv.ipsat.utils.LocalData
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

class TabAdapter(
    private var categories: List<ChannelCategoryItem>,
    private var channelCategories: ChannelCategory,
    private var eventList: EventModel,
    fragmentManager: FragmentActivity,
) : FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return categories.size
    }

    override fun createFragment(position: Int): Fragment {
        val hasChannels = eventList.any { it.category == categories[position].id }
        if (!hasChannels) {
            val item = ChannelCategoryScreen()
            val bundle = Bundle()
            bundle.putSerializable("listData", channelCategories)
            bundle.putSerializable("data", categories[position])
            item.arguments = bundle
            return item
        } else {
            val item = EventChannelScreen()
            val bundle = Bundle()
            bundle.putSerializable("listData", channelCategories)
            LocalData.eventList.clear()
            LocalData.eventList.addAll(eventList)
            bundle.putSerializable("data", categories[position])
            item.arguments = bundle
            return item
        }
    }


}