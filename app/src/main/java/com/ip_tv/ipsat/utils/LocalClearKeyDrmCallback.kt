/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.utils

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.DefaultDrmSessionManagerProvider
import com.google.android.exoplayer2.drm.ExoMediaDrm
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.LocalMediaDrmCallback
import com.google.android.exoplayer2.drm.MediaDrmCallback
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.ip_tv.ipsat.domain.model.EventModelItem
import com.ip_tv.ipsat.presentation.activities.SignedCookieDataSourceFactory
import java.util.*
import java.util.Base64

class LocalClearKeyDrmCallback(private val keyJson: String) : MediaDrmCallback {
    override fun executeProvisionRequest(
        uuid: UUID,
        request: ExoMediaDrm.ProvisionRequest
    ): ByteArray {
        throw UnsupportedOperationException("Provisioning not supported for ClearKey")
    }

    override fun executeKeyRequest(uuid: UUID, request: ExoMediaDrm.KeyRequest): ByteArray {
        return keyJson.toByteArray(Charsets.UTF_8)
    }
}

fun hexToBase64(hex: String): String {
    val bytes = hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
}

fun playWithClearKeyAndCookie(player: ExoPlayer, url: EventModelItem) {
    val mediaItemBuilder = MediaItem.Builder()
        .setUri(url.play_url)
        .setMimeType(MimeTypes.APPLICATION_MPD)

    var drmSessionManager: DefaultDrmSessionManager? = null
    if (!url.clearkey.isNullOrBlank()) {
        val (kidHex, keyHex) = url.clearkey.split(":")
        val kidBase64 = hexToBase64(kidHex)
        val keyBase64 = hexToBase64(keyHex)

        val clearKeyJson = """
        {
          "keys": [
            {
              "kty": "oct",
              "kid": "$kidBase64",
              "k": "$keyBase64"
            }
          ],
          "type": "temporary"
        }
        """.trimIndent()

        val drmCallback = LocalClearKeyDrmCallback(clearKeyJson)
        drmSessionManager = DefaultDrmSessionManager.Builder()
            .build(drmCallback)

        mediaItemBuilder.setDrmConfiguration(
            MediaItem.DrmConfiguration.Builder(C.CLEARKEY_UUID)
                .setLicenseUri("") // Important for ClearKey

                .setForceDefaultLicenseUri(true) // Fixes provisioning error
                .build()
        )
    }

    val mediaItem = mediaItemBuilder.build()

    Log.d("GG  COOKIE", "loadChannel: $url")

    val dataSourceFactory = DefaultHttpDataSource.Factory()


    val mediaSource = DashMediaSource.Factory(dataSourceFactory)
        .setDrmSessionManagerProvider { drmSessionManager!! }
        .createMediaSource(mediaItem)

    player.setMediaSource(mediaSource)

    player.prepare()
}

fun createDrmCallbackFromString(clearkeyRaw: String): LocalMediaDrmCallback {
    val (kidHex, keyHex) = clearkeyRaw.split(":").map { it.trim() }

    val kidB64 = hexToBase64(kidHex)
    val keyB64 = hexToBase64(keyHex)

    val json = """
        {
          "keys": [{
            "kty": "oct",
            "kid": "$kidB64",
            "k": "$keyB64"
          }],
          "type": "temporary"
        }
    """.trimIndent().toByteArray()

    return LocalMediaDrmCallback(json)
}
