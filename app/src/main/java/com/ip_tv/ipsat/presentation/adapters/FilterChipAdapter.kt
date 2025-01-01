package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.databinding.ItemChipBinding

class FilterChipAdapter(
    private val items: List<String>,
    private val selectedItems: MutableList<String>,
    private val onChipClick: (List<String>) -> Unit
) : RecyclerView.Adapter<FilterChipAdapter.FilterChipViewHolder>() {

    inner class FilterChipViewHolder(val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.root.text = item
            binding.root.isChecked = selectedItems.contains(item)

            // Set up click listener to remove the item
            binding.root.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Item selected
                    if (!selectedItems.contains(item)) {
                        selectedItems.add(item)
                    }
                } else {
                    // Item deselected
                    selectedItems.remove(item)
                }
                onChipClick(selectedItems)  // Notify the RecyclerView to update
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterChipViewHolder {
        val binding = ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterChipViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size
}