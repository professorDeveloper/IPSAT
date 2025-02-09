package com.ip_tv.ipsat.domain.repository

import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.ChannelLinkResponse
import com.ip_tv.ipsat.domain.model.ChannelResponse
import com.ip_tv.ipsat.domain.model.SubCategory
import kotlinx.coroutines.flow.Flow

interface LiveTvRepository {
    fun getLiveTvCategories(): Flow<Result<ChannelCategory>>
    fun getChannelUrl(channelId:String): Flow<Result<ChannelLinkResponse>>
    fun loadAllSubCategory(): Flow<Result<SubCategory>>
    fun loadChannelByCategory(categoryId: Int): Flow<Result<ChannelResponse>>

}