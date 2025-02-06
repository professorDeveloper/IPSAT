package com.ip_tv.ipsat.domain.model

data class MovieResponse(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<Movie>
)