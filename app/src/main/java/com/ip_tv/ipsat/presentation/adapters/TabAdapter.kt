package com.ip_tv.ipsat.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ip_tv.ipsat.presentation.screens.MovieVodScreen

class TabAdapter(var arrayList: ArrayList<String>, fragmentManager: FragmentActivity) :
    FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MovieVodScreen()
            else -> MovieVodScreen()
        }
    }
}