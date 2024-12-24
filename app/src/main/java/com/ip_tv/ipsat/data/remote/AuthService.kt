package com.ip_tv.ipsat.data.remote

import com.ip_tv.ipsat.domain.model.LoginResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
interface AuthService {

    @GET("activate_subscription")
    suspend fun activateSubscription(
        @Query("code") code: String,
        @Query("mac_address") macAddress: String
    ): Response<LoginResponse>
}

