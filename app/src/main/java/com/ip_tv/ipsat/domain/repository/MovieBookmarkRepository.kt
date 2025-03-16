/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.domain.repository

import com.ip_tv.ipsat.data.local.entity.MovieBookmark

interface MovieBookmarkRepository {
    suspend fun addBookmark(movie: MovieBookmark)

    suspend fun removeBookmark(movie: MovieBookmark)
    suspend fun isBookmarked(movieId: Int): Boolean
    suspend fun getAllBookmarks(): List<MovieBookmark>

    suspend fun clearAllBookmarks()
}