package com.ip_tv.ipsat.domain.model

data class VodMovieResponse(
    val authInfo: String,
    val subtitleList: Any,
    val urlobj: List<Urlobj>
)