/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.data.remote.CastItem
import com.ip_tv.ipsat.data.remote.CastResponse
import com.ip_tv.ipsat.databinding.ItemCastBinding
import com.ip_tv.ipsat.domain.model.Cast
import com.ip_tv.ipsat.utils.loadImage

class CastDetailAdapter : RecyclerView.Adapter<CastDetailAdapter.CastViewHolder>() {
    private val list = ArrayList<CastItem>()



    inner class CastViewHolder(private val binding: ItemCastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CastItem) {
            binding.apply {
                castImage.loadImage(data.imageUrl)
                castCharacter.text = "Character:${data.character}"
                castName.text = data.name
                binding.root.setOnClickListener {
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder(
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

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.onBind(list.get(position))
    }

    fun submitList(newList: List<CastItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
