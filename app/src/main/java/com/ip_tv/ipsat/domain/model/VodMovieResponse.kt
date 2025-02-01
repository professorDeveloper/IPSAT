package com.ip_tv.ipsat.domain.model

import java.io.Serializable

data class VodMovieResponse(
    val authInfo: String,
    @Transient val subtitleList: Any,  // Ignored during serialization
    val urlobj: List<Urlobj>
) : Serializable
