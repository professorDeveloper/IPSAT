/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ip_tv.ipsat.data.local.entity.MovieBookmark

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromEpisodeList(value: ArrayList<MovieBookmark>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toEpisodeList(value: String): ArrayList<MovieBookmark> {
        val listType = object : TypeToken<ArrayList<MovieBookmark>>() {}.type
        return gson.fromJson(value, listType)
    }
}
