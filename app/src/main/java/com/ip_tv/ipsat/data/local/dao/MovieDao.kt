package com.ip_tv.ipsat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ip_tv.ipsat.data.local.entity.MovieBookmark

@Dao
interface MovieDao {

    // Insert a bookmark
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(movie: MovieBookmark)

    // Remove a bookmark
    @Delete
    suspend fun removeBookmark(movie: MovieBookmark)

    // Check if a movie is bookmarked
    @Query("SELECT EXISTS(SELECT 1 FROM moviebookmark WHERE id = :movieId)")
    suspend fun isBookmarked(movieId: Int): Boolean


    // Get all bookmarks
    @Query("SELECT * FROM moviebookmark")
    suspend fun getAllBookmarks(): List<MovieBookmark>

    // Clear all bookmarks
    @Query("DELETE FROM moviebookmark")
    suspend fun clearAllBookmarks(): Int // Return the number of rows deleted
}
