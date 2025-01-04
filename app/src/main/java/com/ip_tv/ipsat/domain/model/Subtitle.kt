package com.ip_tv.ipsat.domain.model

data class Subtitle(
    val key: String,
    val language: String,
    val languageShort: String,
    val list: List<String>,
    val source: String,
    val value: String
)