package com.ip_tv.ipsat.domain.repository

import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.domain.model.VodMovieResponse
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun getSeriesContent(id: Int): Flow<Result<SeriesDetailResponse>>
    fun getMovieResponse(id: Int): Flow<Result<VodMovieResponse>>
    fun getSeriesVod(id: Int): Flow<Result<VodMovieResponse>>
}