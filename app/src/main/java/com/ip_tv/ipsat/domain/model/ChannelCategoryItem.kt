package com.ip_tv.ipsat.domain.model

import java.io.Serializable

data class ChannelCategoryItem(
    val id: Int,
    val name: String,
    val programsum: Int,
    val type: String
) : Serializable