package com.ip_tv.ipsat.domain.repository

import android.net.MacAddress
import com.ip_tv.ipsat.domain.model.LoginResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun activateCode(code:String,macAddress:String):Flow<Result<LoginResponse>>
}