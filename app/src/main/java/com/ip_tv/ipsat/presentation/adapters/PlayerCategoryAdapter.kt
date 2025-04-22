package com.ip_tv.ipsat.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.PlayerCategoryItemBinding
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem

class PlayerCategoryAdapter(
    private val onCategorySelected: (ChannelCategoryItem) -> Unit,
) : RecyclerView.Adapter<PlayerCategoryAdapter.PlayerCategoryVh>() {

    private val list = ArrayList<ChannelCategoryItem>()
    var selectedPosition = -1

    inner class PlayerCategoryVh(private val binding: PlayerCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun onBind(item: ChannelCategoryItem, isSelected: Boolean) {
            binding.root.apply {
                text = item.name

                setBackgroundColor(
                    if (isSelected) {
                        setTextColor(context.getColor(R.color.whiteMain))
                        resources.getColor(R.color.colorPrimary)
                    } else {
                        setTextColor(context.getColor(R.color.textLightColor))
                        resources.getColor(R.color.whiteGray)
                    }
                )

                setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    onCategorySelected(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerCategoryVh {
        return PlayerCategoryVh(
            PlayerCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: PlayerCategoryVh, position: Int) {
        holder.onBind(list[position], position == selectedPosition)
    }

    fun updateCategories(categories: List<ChannelCategoryItem>) {
        list.clear()
        list.addAll(categories)
        notifyDataSetChanged()
    }

    fun setDefaultSelected(category: ChannelCategoryItem): Int {
        val index = list.indexOfFirst { it.id == category.id } // Match by ID or unique property
        if (index != -1) {
            selectedPosition = index
            notifyItemChanged(selectedPosition)
        }
        return index
    }
}
