package com.ip_tv.ipsat.domain.model

import java.io.Serializable

data class Movie(
    val categoryProperty: String?,
    val categoryid: String,
    val country: String,
    val description: String,
    val id: Int,
    val image: String,
    val language: String,
    val name: String,
    val rating: Double,
    val release_year: String
):Serializable


