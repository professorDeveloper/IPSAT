package com.ip_tv.ipsat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "moviebookmark")
data class MovieBookmark(
    @PrimaryKey
    val id: Int,
    val title: String,
    val image: String,
    val categoryProperty: String?,
    val categoryid: String?,
    val country: String?,
    val description: String?,
    val language: String?,
    val rating: Double = 0.0,
    val release_year: String?
)
