package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ip_tv.ipsat.databinding.ItemCastBinding
import com.ip_tv.ipsat.domain.model.Cast
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.setAnimation

class CastAdapter():RecyclerView.Adapter<CastAdapter.CastVh>() {
    private val list:ArrayList<Cast> = ArrayList()
    inner class CastVh(private val binding:ItemCastBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(data: Cast){
            binding.apply {
                castName.text=data.name
                Glide.with(binding.root.context)
                    .load(data.image)
                    .into(castImage)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastVh {
        return  CastVh(
            ItemCastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    fun submitList(data:ArrayList<Cast>){
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CastVh, position: Int) {
        holder.onBind(list.get(position))
    }
}