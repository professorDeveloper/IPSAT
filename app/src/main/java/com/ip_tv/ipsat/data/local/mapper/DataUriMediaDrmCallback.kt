/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.data.local.mapper

import android.util.Base64
import com.google.android.exoplayer2.drm.ExoMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.drm.MediaDrmCallback
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import java.util.UUID

class DataUriMediaDrmCallback(
    private val licenseUri: String,
    private val defaultHttpDataSourceFactory: DefaultHttpDataSource.Factory
) : MediaDrmCallback {

    override fun executeKeyRequest(uuid: UUID, request: ExoMediaDrm.KeyRequest): ByteArray {
        return if (licenseUri.startsWith("data:")) {
            // Extract and decode the Base64 part of the data URL.
            val base64Part = licenseUri.substringAfter("base64,")
            Base64.decode(base64Part, Base64.NO_WRAP)
        } else {
            // Fallback to HTTP-based callback if the URL is not a data scheme.
            val httpCallback = HttpMediaDrmCallback(licenseUri, defaultHttpDataSourceFactory)
            httpCallback.executeKeyRequest(uuid, request)
        }
    }

    override fun executeProvisionRequest(uuid: UUID, request: ExoMediaDrm.ProvisionRequest): ByteArray {
        // Generally, provision requests are fetched over HTTP.
        val httpCallback = HttpMediaDrmCallback(request.defaultUrl, defaultHttpDataSourceFactory)
        return httpCallback.executeProvisionRequest(uuid, request)
    }
}
