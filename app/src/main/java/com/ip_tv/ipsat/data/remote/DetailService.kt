package com.ip_tv.ipsat.data.remote

import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.domain.model.VodMovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DetailService {
    @GET("get_series_details/")
    suspend fun getSeriesDetail(
        @Query("subscription_code") subscriptionCode: String,
        @Query("contentId") contentId: String,
    ): Response<SeriesDetailResponse>

    @GET("get_vod_terminal_state/")
    suspend fun getMoviesDetail(
        @Query("subscription_code") subscriptionCode: String,
        @Query("id") contentId: String,
    ): Response<VodMovieResponse>

    @GET("get_series_terminal_state")
    suspend fun getSeriesVod(
        @Query("subscription_code") subscriptionCode: String,
        @Query("id") contentId: String,
    ): Response<VodMovieResponse>
}