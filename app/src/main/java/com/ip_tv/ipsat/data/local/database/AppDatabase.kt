package com.ip_tv.ipsat.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ip_tv.ipsat.data.local.converter.Converters
import com.ip_tv.ipsat.data.local.dao.MovieDao
import com.ip_tv.ipsat.data.local.entity.MovieBookmark

@Database(
    entities = [MovieBookmark::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
