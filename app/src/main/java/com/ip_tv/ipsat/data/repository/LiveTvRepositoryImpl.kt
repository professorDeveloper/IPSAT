package com.ip_tv.ipsat.data.repository

import com.ip_tv.ipsat.data.remote.LiveTvService
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.ChannelLinkResponse
import com.ip_tv.ipsat.domain.model.ChannelResponse
import com.ip_tv.ipsat.domain.model.ErrorResponse
import com.ip_tv.ipsat.domain.model.SubCategory
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.domain.repository.LiveTvRepository
import com.ip_tv.ipsat.utils.toDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LiveTvRepositoryImpl @Inject constructor(
    private val liveTvService: LiveTvService,
    private val userPreferenceManager: UserPreferenceManager
) : LiveTvRepository {
    override fun getLiveTvCategories() = flow<Result<ChannelCategory>> {
        val response =
            liveTvService.getChannelCategories(subscriptionCode = userPreferenceManager.subCode)
        if (response.isSuccessful) {
            emit(Result.success(response.body()!!))
        } else {
            val errorResponse =
                response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
            emit(Result.failure(Exception(errorResponse.message)))
        }
    }.flowOn(Dispatchers.IO)

    override fun getChannelUrl(channelId: String) = flow<Result<ChannelLinkResponse>> {
        val response = liveTvService.getChannelUrl(userPreferenceManager.subCode, channelId.toInt())
        if (response.isSuccessful) {
            emit(Result.success(response.body()!!))
        } else {
            val errorResponse =
                response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
            emit(Result.failure(Exception(errorResponse.message)))
        }
    }.flowOn(Dispatchers.IO)

    override fun loadAllSubCategory() = flow<Result<SubCategory>> {
        val response =
            liveTvService.getSubCategories(subscriptionCode = userPreferenceManager.subCode)
        if (response.isSuccessful) {
            emit(Result.success(response.body()!!))
        } else {
            val errorResponse =
                response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
            emit(Result.failure(Exception(errorResponse.message)))
        }
    }.flowOn(Dispatchers.IO)

    override fun loadChannelByCategory(categoryId: Int) = flow<Result<ChannelResponse>> {
        val response =
            liveTvService.getChannels(
                categoryId = categoryId,
                subscriptionCode = userPreferenceManager.subCode
            )
        if (response.isSuccessful) {
            emit(Result.success(response.body()!!))
        } else {
            val errorResponse =
                response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
            emit(Result.failure(Exception(errorResponse.message)))
        }
    }.flowOn(Dispatchers.IO)
}