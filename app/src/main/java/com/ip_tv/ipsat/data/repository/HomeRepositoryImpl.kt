package com.ip_tv.ipsat.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ip_tv.ipsat.data.remote.MovieService
import com.ip_tv.ipsat.domain.model.ErrorResponse
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.domain.repository.HomeRepository
import com.ip_tv.ipsat.utils.toDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val movieService: MovieService,
    private val userPreferenceManager: UserPreferenceManager
) :
    HomeRepository {
    override fun loadBanner() = flow {
        val random = (1..100).random()
        val response = movieService.getMovies(
            subscriptionCode = userPreferenceManager.subCode,
            page = random,
            pageSize = 20
        )
        if (response.isSuccessful) {
            val newList = arrayListOf<Movie>()
            response.body()!!.results.forEach {
                newList.add(it)
            }
            emit(Result.success(newList))
        } else {
            if (response.code() == 404) {
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
    }

    override suspend fun getMovies(page: Int) = flow<Result<ArrayList<Movie>>> {
        val response  =movieService.getMovies(
            subscriptionCode = userPreferenceManager.subCode,
            page=page,
            pageSize = 60,
        )
        if (response.isSuccessful) {
             val newList =ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            emit(Result.success(newList))
        }else {
            if (response.code() ==404|| response.code()==403) {
                val errorResponse = response.errorBody()?.string()
                val jsonObject = JSONObject(errorResponse)
                val errorDetail = jsonObject.optString("detail")
                emit(Result.failure(Exception(errorDetail)))
            }else {
                val errorResponse =
                    response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
                emit(Result.failure(Exception(errorResponse.message)))

            }
        }

    }

    override suspend fun getSeries(page: Int)=flow<Result<ArrayList<Movie>>> {
        val response  =movieService.getSeries(
            subscriptionCode = userPreferenceManager.subCode,
            page=page,
            pageSize = 60,
        )
        if (response.isSuccessful) {
            val newList =ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            emit(Result.success(newList))
        }else {
            if (response.code() ==404|| response.code()==403) {
                val errorResponse = response.errorBody()?.string()
                val jsonObject = JSONObject(errorResponse)
                val errorDetail = jsonObject.optString("detail")
                emit(Result.failure(Exception(errorDetail)))
            }else {
                val errorResponse =
                    response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
                emit(Result.failure(Exception(errorResponse.message)))
            }
        }
    }

    override suspend fun getDocuments(page: Int)=flow<Result<ArrayList<Movie>>> {
        val response = movieService.getDocumentary(
            subscriptionCode = userPreferenceManager.subCode,
            page = page,
            pageSize = 60
        )
        if (response.isSuccessful) {
            val newList =ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            emit(Result.success(newList))
        }else {
            if (response.code() ==404|| response.code()==403) {
                val errorResponse = response.errorBody()?.string()
                val jsonObject = JSONObject(errorResponse)
                val errorDetail = jsonObject.optString("detail")
                emit(Result.failure(Exception(errorDetail)))
            }else {
                val errorResponse =
                    response.errorBody()?.string().toString().toDataClass<ErrorResponse>()
                emit(Result.failure(Exception(errorResponse.message)))
            }
        }
    }
}