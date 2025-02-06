package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.CategoryItemBinding
import com.ip_tv.ipsat.domain.model.SubCategoryItem

class CategoryAdapter(
    private val onSelectionChanged: (List<SubCategoryItem>) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryVh>() {

    private val list = ArrayList<SubCategoryItem>()
    private val selectedItems = mutableSetOf<SubCategoryItem>()

    inner class CategoryVh(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: SubCategoryItem) {
            binding.apply {
                categoryTitle.text = data.property_name

                if (selectedItems.contains(data))  {
                    binding.container.setBackgroundResource(R.drawable.tab_selected)
                    binding.categoryTitle.setTextColor(binding.root.context.getColor(R.color.white))
                }else {
                    binding.container.setBackgroundResource(R.drawable.tab_item_unselected)
                    binding.categoryTitle.setTextColor(binding.root.context.getColor(R.color.bg_black_50))
                }

                root.setOnClickListener {
                    if (selectedItems.contains(data)) {
                        selectedItems.remove(data)
                    } else {
                        selectedItems.add(data)
                    }
                    notifyItemChanged(adapterPosition)
                    onSelectionChanged(selectedItems.toList())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVh {
        return CategoryVh(
            CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun submitList(newList: List<SubCategoryItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CategoryVh, position: Int) {
        holder.onBind(list[position])
    }
}
