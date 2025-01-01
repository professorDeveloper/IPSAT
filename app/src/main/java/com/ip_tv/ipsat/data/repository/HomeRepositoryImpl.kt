package com.ip_tv.ipsat.data.repository

import com.ip_tv.ipsat.data.remote.MovieService
import com.ip_tv.ipsat.domain.model.ErrorResponse
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.MovieResponse
import com.ip_tv.ipsat.domain.model.SearchResults
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
        val response = movieService.getMovies(
            subscriptionCode = userPreferenceManager.subCode,
            page = page,
            pageSize = 60,
        )
        if (response.isSuccessful) {
            val newList = ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            emit(Result.success(newList))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    override suspend fun getSeries(page: Int) = flow<Result<ArrayList<Movie>>> {
        val response = movieService.getSeries(
            subscriptionCode = userPreferenceManager.subCode,
            page = page,
            pageSize = 60,
        )
        if (response.isSuccessful) {
            val newList = ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            emit(Result.success(newList))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    override suspend fun getDocuments(page: Int) = flow<Result<ArrayList<Movie>>> {
        val response = movieService.getDocumentary(
            subscriptionCode = userPreferenceManager.subCode,
            page = page,
            pageSize = 60
        )
        if (response.isSuccessful) {
            val newList = ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            emit(Result.success(newList))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    override suspend fun getKids(page: Int) = flow<Result<ArrayList<Movie>>> {
        val response = movieService.getKids(
            subscriptionCode = userPreferenceManager.subCode,
            page = page,
            pageSize = 60
        )
        if (response.isSuccessful) {
            val newList = ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            emit(Result.success(newList))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    override suspend fun filterMovies(results: SearchResults) = flow<Result<SearchResults>> {
        val tags = results.genres?.map { "$it," }?.joinToString("")
        val response =
            movieService.filterMovies(
                subscriptionCode = userPreferenceManager.subCode,
                page = results.page,
                country = results.country ?: "All",
                rating = results.rating ?: "All",
                categoryProperty = tags ?: "All",
                releaseYear = if (results.releaseYear == -1) "All" else results.releaseYear.toString(),
                pageSize = 60
            )

        if (response.isSuccessful) {
            val newList = ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            results.results = newList
            emit(Result.success(results))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    override suspend fun filterDocumentary(results: SearchResults) = flow<Result<SearchResults>> {
        val tags = results.genres?.map { "$it," }?.joinToString("")
        val response =
            movieService.filterDocumentary(
                subscriptionCode = userPreferenceManager.subCode,
                page = results.page,
                country = results.country ?: "All",
                rating = results.rating ?: "All",
                categoryProperty = tags ?: "All",
                releaseYear = if (results.releaseYear == -1) "All" else results.releaseYear.toString(),
                pageSize = 60
            )

        if (response.isSuccessful) {
            val newList = ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            results.results = newList
            emit(Result.success(results))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    override suspend fun filterKids(results: SearchResults) = flow<Result<SearchResults>> {
        val tags = results.genres?.map { "$it," }?.joinToString("")
        val response =
            movieService.filterKids(
                subscriptionCode = userPreferenceManager.subCode,
                page = results.page,
                country = results.country ?: "All",
                rating = results.rating ?: "All",
                categoryProperty = tags ?: "All",
                releaseYear = if (results.releaseYear == -1) "All" else results.releaseYear.toString(),
                pageSize = 60
            )

        if (response.isSuccessful) {
            val newList = ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            results.results = newList
            emit(Result.success(results))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    override suspend fun filterSeries(results: SearchResults) = flow<Result<SearchResults>> {
        val tags = results.genres?.map { "$it," }?.joinToString("")
        val response =
            movieService.filterSeries(
                subscriptionCode = userPreferenceManager.subCode,
                page = results.page,
                country = results.country ?: "All",
                rating = results.rating ?: "All",
                categoryProperty = tags ?: "All",
                releaseYear = if (results.releaseYear == -1) "All" else results.releaseYear.toString(),
                pageSize = 60
            )

        if (response.isSuccessful) {
            val newList = ArrayList<Movie>()
            response.body()?.results?.onEach {
                newList.add(it)
            }
            results.results = newList
            emit(Result.success(results))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    override suspend fun search(query: String) = flow<Result<MovieResponse>> {
        val response = movieService.search(
            subscriptionCode = userPreferenceManager.subCode,
            query = query
        )
        if (response.isSuccessful) {
            emit(Result.success(response.body()!!))
        } else {
            if (response.code() == 404 || response.code() == 403) {
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

    }.flowOn(Dispatchers.IO)
}