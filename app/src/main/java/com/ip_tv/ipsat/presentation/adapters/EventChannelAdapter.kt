/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.Target
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ItemChannelBinding
import com.ip_tv.ipsat.domain.model.EventModelItem
import com.ip_tv.ipsat.utils.LocalData


class EventChannelAdapter :
    ListAdapter<EventModelItem, EventChannelAdapter.ChannelVh>(ChannelDiffUtil()) {

    private lateinit var channelItemClickListener: (EventModelItem) -> Unit

    fun setChannelItemClickListener(listener: (EventModelItem) -> Unit) {
        channelItemClickListener = listener
    }

    inner class ChannelVh(private val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: EventModelItem) {
            binding.apply {

                val glideUrl = GlideUrl(
                    item.logo, LazyHeaders.Builder() // mimic a real browser
                        .addHeader(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36"
                        ) // some hosts also want a Referer
                        .addHeader("Referer", "https://seeklogo.com/")
                        .build()
                )

                Glide.with(binding.root.context)
                    .load(glideUrl)
                    .into(binding.coverImage)
                title.text = item.name
                binding.root.setOnClickListener {
                    channelItemClickListener.invoke(item)
                }
                setAnimation(binding.root)
            }
        }
    }

    fun query(query: String) {
        if (LocalData.selectedCategory.isEmpty()) {
            val trimmedQuery = query.trim()
            val filteredList = if (trimmedQuery.isEmpty()) currentList else currentList.filter {
                it.name.contains(
                    trimmedQuery,
                    ignoreCase = true
                )
            }
            if (filteredList != currentList) submitList(filteredList)
        } else {
            val arrayList: ArrayList<EventModelItem> =
                currentList.toMutableList() as ArrayList<EventModelItem>
//            arrayList.filterByKeywords(LocalData.selectedCategory)
            val trimmedQuery = query.trim()
            val filteredList = if (trimmedQuery.isEmpty()) currentList else arrayList.filter {
                it.name.contains(
                    trimmedQuery,
                    ignoreCase = true
                )
            }
            if (filteredList != arrayList) submitList(filteredList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelVh {
        return ChannelVh(
            ItemChannelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChannelVh, position: Int) {
        holder.onBind(getItem(position))
    }

    class ChannelDiffUtil : DiffUtil.ItemCallback<EventModelItem>() {
        override fun areItemsTheSame(
            oldItem: EventModelItem,
            newItem: EventModelItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: EventModelItem,
            newItem: EventModelItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun setAnimation(view: View) {
        val anim: Animation = AnimationUtils.loadAnimation(view.context, R.anim.scale_animation)
        view.startAnimation(anim)
    }
}
