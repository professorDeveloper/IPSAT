package com.ip_tv.ipsat.presentation.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ChannelMiddleItemBinding
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.utils.dp
import com.ip_tv.ipsat.utils.loadImage

class PlayerChannelAdapter(
) : RecyclerView.Adapter<PlayerChannelAdapter.PlayerChannelVh>() {

    private var list = ArrayList<ChannelResponseItem>()
    private var selectedPosition = -1
    private lateinit var onChannelSelected: (ChannelResponseItem) -> Unit
    fun setItemChannelClickListener(listener: (ChannelResponseItem) -> Unit) {
        onChannelSelected = listener
    }

    inner class PlayerChannelVh(private val binding: ChannelMiddleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun onBind(channelItem: ChannelResponseItem, isSelected: Boolean) {
            binding.apply {
                channelTitle.text = channelItem.name
                channelImg.loadImage(channelItem.image)
                if (isSelected) {
                    binding.root.setStrokeColor(
                        ColorStateList.valueOf(
                            binding.root.context.getColor(R.color.colorPrimary)
                        )
                    )
                    binding.root.strokeWidth = 2
                } else {
                    binding.root.strokeWidth = 0
                }

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onChannelSelected(channelItem)
                }
            }
        }
    }

    fun submitList(newList: ArrayList<ChannelResponseItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedPosition = -1
        notifyDataSetChanged()
    }

    fun setDefaultSelected(channelItem: ChannelResponseItem): Int {
        val index = list.indexOfFirst { it.id == channelItem.id }
        if (index != -1) {
            selectedPosition = index
            notifyItemChanged(selectedPosition)
        }
        return index
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
        holder.onBind(list[position], position == selectedPosition)
    }
}
