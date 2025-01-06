package com.ip_tv.ipsat.domain.model

data class Urlobj(
    val encryptKey: String,
    val encryptType: Int,
    val hdtv: String,
    val playUrl: String,
    val playflag: Int,
    val thumbnailUrl: String,
    val typeName: String
)