package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.databinding.ItemCastBinding
import com.ip_tv.ipsat.domain.model.Cast
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.setAnimation

class CastAdapter(private val list:ArrayList<Cast>):RecyclerView.Adapter<CastAdapter.CastVh>() {
    inner class CastVh(private val binding:ItemCastBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(data: Cast){
            binding.apply {
                setAnimation(binding.root.context,binding.root)
                castName.text=data.name
                castImage.loadImage(data.image)
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



    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CastVh, position: Int) {
        holder.onBind(list.get(position))
    }
}