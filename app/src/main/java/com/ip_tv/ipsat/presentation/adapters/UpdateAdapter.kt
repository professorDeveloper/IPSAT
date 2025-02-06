package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.databinding.ItemUpdateBinding
import com.ip_tv.ipsat.domain.model.Message
import com.ip_tv.ipsat.utils.toReadableDateTime

class UpdateAdapter  :RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder>() {
    private val list =ArrayList<Message>()
    inner class  UpdateViewHolder (private val binding: ItemUpdateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data:Message){
            binding.apply {
                binding.notificationDate.text= data.created_at.toReadableDateTime()
                binding.notificationBody.text=data.body
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateViewHolder {
        return UpdateViewHolder(ItemUpdateBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(newList:List<Message>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: UpdateViewHolder, position: Int) {
        holder.onBind(list[position])
    }
}