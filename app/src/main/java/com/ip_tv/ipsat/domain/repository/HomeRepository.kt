package com.ip_tv.ipsat.domain.repository

import androidx.paging.PagingSource
import com.ip_tv.ipsat.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun loadBanner(): Flow<Result<ArrayList<Movie>>>
  suspend  fun getMovies(page: Int):Flow<Result<ArrayList<Movie>>>

  suspend fun getSeries(page: Int):Flow<Result<ArrayList<Movie>>>
}