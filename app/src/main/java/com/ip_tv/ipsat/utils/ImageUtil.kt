/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import coil.Coil
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.loader.ImageLoader
import com.zen.overlapimagelistview.OverlapImageListView

object ImageUtil {

    fun init(context: Context) {
        val imageLoader = coil.ImageLoader.Builder(context).componentRegistry {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) add(ImageDecoderDecoder(context))
            else add(coil.decode.GifDecoder())

            add(SvgDecoder(context))
        }.build()

        Coil.setImageLoader(imageLoader)
    }

    fun loadImage(context: Context, url: String, imageView: AppCompatImageView) {
        imageView.load(url)
    }

    fun loadImage(context: Context, resourceId: Int, imageView: AppCompatImageView) {
        imageView.load(resourceId)
    }

    fun loadImage(context: Context, uri: Uri, imageView: AppCompatImageView) {
        imageView.load(uri)
    }

//    fun loadCircleImage(context: Context, url: String, imageView: AppCompatImageView)  {
//        imageView.background = ContextCompat.getDrawable(context, R.drawable.shape_oval_with_border)
//        imageView.backgroundTintList = ColorStateList.valueOf(context.getAttrValue(R.attr.themeSmallNegativeFont))
//        imageView.setPadding(context.resources.getDimensionPixelSize(R.dimen.lineWidth))
//        imageView.load(url) {
//            transformations(CircleCropTransformation())
//        }
//    }
//
//    fun loadRectangleImage(context: Context, url: String, imageView: AppCompatImageView) {
//        imageView.background = ContextCompat.getDrawable(context, R.drawable.shape_rectangle)
//        imageView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.transparent))
//        imageView.load(url)
//    }

    fun showFullScreenImage(
        context: Context, url: ArrayList<String>, imageView: ImageView, position: Int
    ) {
        val builder = StfalconImageViewer.Builder(context, url) { view, image ->
            view.loadImage(image)
        }.withStartPosition(position)
            .withHiddenStatusBar(false)

        if (imageView.parent == null) {
            builder.withTransitionFrom(imageView)
        }

        builder.show(true)
    }


    fun loadImagesIntoOverlapImageListView(
        context: Context, urls: List<String>, overlapImageListView: OverlapImageListView
    ) {
        val bitmaps = ArrayList<Bitmap>()

        urls.forEach {
            val loader = coil.ImageLoader(context)

            val request =
                ImageRequest.Builder(context).data(it).transformations(CircleCropTransformation())
                    .target { drawable ->
                        bitmaps.add((drawable as BitmapDrawable).bitmap)

                        if (bitmaps.size == overlapImageListView.circleCount) {
                            overlapImageListView.imageList = bitmaps
                        }
                    }.build()

            loader.enqueue(request)
        }
    }
}