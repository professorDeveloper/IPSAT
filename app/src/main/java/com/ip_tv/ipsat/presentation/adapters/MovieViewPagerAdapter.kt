package com.ip_tv.ipsat.presentation.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ip_tv.ipsat.presentation.screens.MovieTabScreen

class MovieViewPagerAdapter(fragment: Fragment, val tabList :ArrayList<String>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return tabList.size
    }

    override fun createFragment(position: Int): Fragment {
        val tabScreen = MovieTabScreen()
        val binding  = tabList[position]
        tabScreen.arguments = Bundle().apply {
            putString("tab", binding)
        }
        return tabScreen
    }
}