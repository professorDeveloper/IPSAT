package com.ip_tv.ipsat.domain.repository

import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.SubCategory
import kotlinx.coroutines.flow.Flow

interface LiveTvRepository {
    fun getLiveTvCategories(): Flow<Result<ChannelCategory>>
    fun loadAllSubCategory(): Flow<Result<SubCategory>>

}