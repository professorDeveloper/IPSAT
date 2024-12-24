package com.ip_tv.ipsat.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun activateCode(code:String):Flow<Result<String>>
}