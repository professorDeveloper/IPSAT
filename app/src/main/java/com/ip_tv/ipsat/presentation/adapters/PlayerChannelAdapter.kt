package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.databinding.ChannelMiddleItemBinding
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.utils.loadImage

class PlayerChannelAdapter : RecyclerView.Adapter<PlayerChannelAdapter.PlayerChannelVh>() {
    private var list = ArrayList<ChannelResponseItem>()

    inner class PlayerChannelVh(private val binding: ChannelMiddleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(channelItem: ChannelResponseItem) {
            binding.apply {
                channelTitle.text = channelItem.name
                channelImg.loadImage(channelItem.image)
            }
        }
    }

    fun submitList(newList: ArrayList<ChannelResponseItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerChannelVh {
        return PlayerChannelVh(
            ChannelMiddleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PlayerChannelVh, position: Int) {
        holder.onBind(list.get(position))
    }
}