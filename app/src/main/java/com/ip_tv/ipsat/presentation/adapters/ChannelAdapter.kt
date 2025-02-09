package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ItemChannelBinding
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.filterByKeywords
import com.ip_tv.ipsat.utils.loadImage
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

class ChannelAdapter :
    ListAdapter<ChannelResponseItem, ChannelAdapter.ChannelVh>(ChannelDiffUtil()) {

    private lateinit var channelItemClickListener: (ChannelResponseItem) -> Unit

    fun setChannelItemClickListener(listener: (ChannelResponseItem) -> Unit) {
        channelItemClickListener = listener
    }

    inner class ChannelVh(private val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: ChannelResponseItem) {
            binding.apply {
                coverImage.loadImage(item.image)
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
            val arrayList: ArrayList<ChannelResponseItem> =
                currentList.toMutableList() as ArrayList<ChannelResponseItem>
            arrayList.filterByKeywords(LocalData.selectedCategory)
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

    class ChannelDiffUtil : DiffUtil.ItemCallback<ChannelResponseItem>() {
        override fun areItemsTheSame(
            oldItem: ChannelResponseItem,
            newItem: ChannelResponseItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ChannelResponseItem,
            newItem: ChannelResponseItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun setAnimation(view: View) {
        val anim: Animation = AnimationUtils.loadAnimation(view.context, R.anim.scale_animation)
        view.startAnimation(anim)
    }
}
