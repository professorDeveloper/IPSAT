package com.ip_tv.ipsat.data.remote

import com.ip_tv.ipsat.domain.model.MovieResponse
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DetailService {
    @GET("get_series_details/")
    suspend fun getSeriesDetail(
        @Query("subscription_code") subscriptionCode: String,
        @Query("contentId") contentId: String,
    ): Response<SeriesDetailResponse>
}