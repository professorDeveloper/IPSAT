package com.ip_tv.ipsat.domain.repository

import com.ip_tv.ipsat.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun loadBanner(): Flow<Result<ArrayList<Movie>>>
}