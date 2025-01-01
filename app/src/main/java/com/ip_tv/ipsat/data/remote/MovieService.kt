package com.ip_tv.ipsat.data.remote

import com.ip_tv.ipsat.domain.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieService {
    @GET("get_movies/")
   suspend fun getMovies(
        @Query("subscription_code") subscriptionCode: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
    ): Response<MovieResponse>

    @GET("get_series/")
    suspend fun getSeries(
        @Query("subscription_code") subscriptionCode: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
    ): Response<MovieResponse>

    @GET("get_documentary/")
    suspend fun getDocumentary(
        @Query("subscription_code") subscriptionCode: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
    ): Response<MovieResponse>
    @GET("get_kids/")
    suspend fun getKids(
        @Query("subscription_code") subscriptionCode: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
    ): Response<MovieResponse>

    @GET("filter_movies/")
    suspend fun filterMovies(
        @Query("subscription_code") subscriptionCode: String,
        @Query("country") country: String,
        @Query("rating") rating: String,
        @Query("categoryProperty") categoryProperty: String,
        @Query("release_year") releaseYear: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
    ): Response<MovieResponse>




}