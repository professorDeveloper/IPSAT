package com.ip_tv.ipsat.domain.repository

import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun getSeriesContent(id: Int): Flow<Result<SeriesDetailResponse>>

}