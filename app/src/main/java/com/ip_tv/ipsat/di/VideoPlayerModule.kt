/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.ip_tv.ipsat.di

import android.app.Application
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object VideoPlayerModule {

    @Provides
    @ViewModelScoped
    fun provideVideoPlayer(app: Application): ExoPlayer {
        val renderersFactory = DefaultRenderersFactory(app)
            .setEnableDecoderFallback(true) // Enable fallback to software decoder
        val trackSelector = DefaultTrackSelector(app).apply {
            parameters = buildUponParameters().setMaxVideoBitrate(5000000).build()
        }


        return ExoPlayer.Builder(app, renderersFactory)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .setTrackSelector(trackSelector)
            .build()
    }

}