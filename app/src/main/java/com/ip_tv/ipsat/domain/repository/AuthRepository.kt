package com.ip_tv.ipsat.domain.repository

import android.net.MacAddress
import com.ip_tv.ipsat.domain.model.ErrorResponse
import com.ip_tv.ipsat.domain.model.GetMessagesResponse
import com.ip_tv.ipsat.domain.model.LoginResponse
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun activateCode(code:String,macAddress:String):Flow<Result<LoginResponse>>
    fun subscriptionDetails():Flow<Result<SubscriptionResponse>>
    fun getMessages() :Flow<Result<GetMessagesResponse>>

}