/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.data.repository

import com.ip_tv.ipsat.data.local.dao.MovieDao
import com.ip_tv.ipsat.data.local.entity.MovieBookmark
import com.ip_tv.ipsat.domain.repository.MovieBookmarkRepository
import javax.inject.Inject

class MovieBookmarkRepositoryImpl @Inject constructor(val dao: MovieDao) : MovieBookmarkRepository {

    override suspend fun addBookmark(movie: MovieBookmark) {
        dao.insertBookmark(movie)
    }

    override suspend fun removeBookmark(movie: MovieBookmark) {
        dao.removeBookmark(movie)
    }

    override suspend fun isBookmarked(movieId: Int): Boolean {
        return dao.isBookmarked(movieId)
    }

    override suspend fun getAllBookmarks(): List<MovieBookmark> {
        return dao.getAllBookmarks()
    }

    override suspend fun clearAllBookmarks() {
        dao.clearAllBookmarks()
    }
}