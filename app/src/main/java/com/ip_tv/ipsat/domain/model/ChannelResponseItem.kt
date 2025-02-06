package com.ip_tv.ipsat.domain.model

data class ChannelResponseItem(
    val audioList: String,
    val category: Int,
    val categoryProperty: String,
    val country: String,
    val description: Any,
    val id: Int,
    val image: String,
    val language: String,
    val name: String,
    val scheduleListUrl: String
)