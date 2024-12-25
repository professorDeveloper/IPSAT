package com.ip_tv.ipsat.data.repository

import com.google.gson.Gson
import com.ip_tv.ipsat.data.remote.AuthService
import com.ip_tv.ipsat.domain.model.ErrorResponse
import com.ip_tv.ipsat.domain.model.LoginResponse
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.domain.repository.AuthRepository
import com.zbekz.tashkentmetro.utils.toDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userPreferenceManager: UserPreferenceManager
) : AuthRepository {
    override fun activateCode(code: String, macAddress: String) = flow<Result<LoginResponse>> {
        val response = authService.activateSubscription(code = code, macAddress = macAddress)
        if (response.isSuccessful) {
                userPreferenceManager.isLogged=true
                userPreferenceManager.subCode=code
                emit(Result.success(response.body()!!))
        } else {
            val errorResponse = response.errorBody()?.string().toString().toDataClass<ErrorResponse>()

            emit(Result.failure(Exception(errorResponse.message)))
        }
    }.flowOn(Dispatchers.IO)

    override fun subscriptionDetails()=flow<Result<SubscriptionResponse>> {
        val response = authService.getSubscriptionDetails(userPreferenceManager.subCode)
        if (response.isSuccessful) {
                emit(Result.success(response.body()!!))
        } else {
            val errorResponse = response.errorBody()?.string().toString().toDataClass<ErrorResponse>()

            emit(Result.failure(Exception(errorResponse.message)))
        }
    }

}