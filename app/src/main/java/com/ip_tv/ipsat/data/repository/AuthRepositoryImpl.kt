package com.ip_tv.ipsat.data.repository

import com.ip_tv.ipsat.data.remote.AuthService
import com.ip_tv.ipsat.domain.model.LoginResponse
import com.ip_tv.ipsat.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(private val authService: AuthService):AuthRepository {
    override fun activateCode(code: String, macAddress: String)=flow<Result<LoginResponse>> {
        val response = authService.activateSubscription(code = code, macAddress = macAddress)
        if (response.isSuccessful) {
            if (response.body()?.status == "error") {
                emit(Result.failure(Exception(response.body()?.message)))
            }else {
                emit(Result.success(response.body()!!))
            }
        } else {
            emit(Result.failure(Exception(response.errorBody()?.string())))
        }
    }.flowOn(Dispatchers.IO)

}