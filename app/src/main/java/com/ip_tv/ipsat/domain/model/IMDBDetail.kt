package com.ip_tv.ipsat.domain.model

data class IMDBDetail(
    val category:String,
    val duration:String,
    val writers:ArrayList<String>,
    val actors:ArrayList<String>,
    val directors:ArrayList<String>
)