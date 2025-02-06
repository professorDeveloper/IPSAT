package com.ip_tv.ipsat.data.remote

import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.SubCategory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LiveTvService {
    @GET("categories/")
    suspend fun getChannelCategories(
        @Query("subscription_code") subscriptionCode: String
    ): Response<ChannelCategory>


    @GET("subcategories/")
    suspend fun getSubCategories(
        @Query("subscription_code") subscriptionCode: String,
    ): Response<SubCategory>
}