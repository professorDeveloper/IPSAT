/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.adapters

import android.content.Context
import android.graphics.Rect
import android.provider.ContactsContract.Contacts.Photo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ip_tv.ipsat.data.remote.PhotoItem
import com.ip_tv.ipsat.data.remote.PhotosResponse
import com.ip_tv.ipsat.databinding.ItemScreenshotBinding

class ScreenshotsAdapter(
    private val context: Context,
) : ListAdapter<PhotoItem, ScreenshotsViewHolder>(ScreenshotsDiffCallback) {
    var onScreenshotClick: ((ImageView, PhotoItem, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotsViewHolder {
        val binding = ItemScreenshotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScreenshotsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScreenshotsViewHolder, position: Int) {
        val screenshot = getItem(position)
        with(holder.binding) {
            Glide.with(context)
                .load(screenshot.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(sivScreenshot)
            root.setOnClickListener { onScreenshotClick?.invoke(sivScreenshot, screenshot,position) }
        }
    }
}

class ScreenshotsViewHolder(
    val binding: ItemScreenshotBinding,
) : RecyclerView.ViewHolder(binding.root)

object ScreenshotsDiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
    override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem.imageUrl == newItem.imageUrl
    }

    override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem == newItem
    }
}

class HorizontalItemDecoration(
    private val spaceSize: Int,
    private val optionalSpaceSize: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                left = spaceSize + optionalSpaceSize
                right = spaceSize
            } else if (itemCount > 0 && itemPosition == itemCount - 1) {
                left = spaceSize
                right = spaceSize + optionalSpaceSize
            } else {
                left = spaceSize
                right = spaceSize
            }
            top = spaceSize
            bottom = spaceSize
        }
    }
}