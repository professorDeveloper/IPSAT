package com.ip_tv.ipsat.data.repository

import com.ip_tv.ipsat.data.remote.DetailService
import com.ip_tv.ipsat.domain.model.ErrorResponse
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.domain.model.VodMovieResponse
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.domain.repository.DetailRepository
import com.ip_tv.ipsat.utils.toDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(private val api:DetailService,private val preferenceManager: UserPreferenceManager):DetailRepository {
    override fun getSeriesContent(id: Int)=flow<Result<SeriesDetailResponse>> {
        val response =api .getSeriesDetail(
            subscriptionCode = preferenceManager.subCode,
            contentId = id.toString())

        if (response.isSuccessful) {
            emit(Result.success(response.body()!!))
        }else if (response.code() == 404 || response.code() == 403) {
            val errorResponse = response.errorBody()?.string()
            val jsonObject = JSONObject(errorResponse)
            val errorDetail = jsonObject.optString("detail")
            emit(Result.failure(Exception(errorDetail)))
        } else {
            val errorResponse =
                response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
            emit(Result.failure(Exception(errorResponse.message)))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMovieResponse(id: Int)=flow<Result<VodMovieResponse>> {
        val response = api.getMoviesDetail(
            subscriptionCode = preferenceManager.subCode,
            contentId = id.toString()
        )
        if (response.isSuccessful) {
            emit(Result.success(response.body()!!))
        } else if (response.code() == 404 || response.code() == 403) {
            val errorResponse = response.errorBody()?.string()
            val jsonObject = JSONObject(errorResponse)
            val errorDetail = jsonObject.optString("detail")
            emit(Result.failure(Exception(errorDetail)))
        } else {
            val errorResponse =
                response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
            emit(Result.failure(Exception(errorResponse.message)))
        }

    }
    .flowOn(Dispatchers.IO)

    override fun getSeriesVod(id: Int): Flow<Result<VodMovieResponse>> {
        return flow<Result<VodMovieResponse>> {
            val response = api.getSeriesVod(
                subscriptionCode = preferenceManager.subCode,
                contentId = id.toString()
            )
            if (response.isSuccessful) {
                emit(Result.success(response.body()!!))
            } else if (response.code() == 404 || response.code() == 403) {
                val errorResponse = response.errorBody()?.string()
                val jsonObject = JSONObject(errorResponse)
                val errorDetail = jsonObject.optString("detail")
                emit(Result.failure(Exception(errorDetail)))
            } else {
                val errorResponse =
                    response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
                emit(Result.failure(Exception(errorResponse.message)))
            }
        }.flowOn(Dispatchers.IO)
    }
}