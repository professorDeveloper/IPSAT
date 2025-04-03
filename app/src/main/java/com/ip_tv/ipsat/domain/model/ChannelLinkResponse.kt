package com.ip_tv.ipsat.domain.model

import com.google.gson.annotations.SerializedName

data class ChannelLinkResponse(
    @SerializedName("CloudFront-Key-Pair-Id") val cloudFrontKeyPairId: String?,
    @SerializedName("CloudFront-Policy") val cloudFrontPolicy: String?,
    @SerializedName("CloudFront-Signature") val cloudFrontSignature: String?,
    @SerializedName("SignedCookie")  val signedCookie: String?,
    val authInfo: String,
    val beginTime: Long,
    val channelName: String,
    val cookieType: String,
    val distributionDomain: String,
    val endTime: Long,
    val expires: Any?,
    val ipAddr: String,
    val md5: Any?,
    val objectKey: String,
    val playUrl: String
)
