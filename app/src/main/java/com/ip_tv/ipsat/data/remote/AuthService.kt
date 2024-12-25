package com.ip_tv.ipsat.data.remote

import com.ip_tv.ipsat.domain.model.LoginResponse
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
interface AuthService {

    @GET("activate_subscription")
    suspend fun activateSubscription(
        @Query("code") code: String,
        @Query("mac_address") macAddress: String
    ): Response<LoginResponse>

    @GET("get_subscription_details/")
    suspend fun getSubscriptionDetails(
        @Query("code") code: String,
    ): Response<SubscriptionResponse>


}

