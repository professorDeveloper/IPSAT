package com.ip_tv.ipsat.domain.model

import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("is_expired") val isExpired: Boolean? = null,
    @SerializedName("is_banned") val isBanned: Boolean? = null,
    @SerializedName("message") val message: String,
    @SerializedName("activated_at") val activatedAt: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("mac_address") val macAddress: String? = null
)
