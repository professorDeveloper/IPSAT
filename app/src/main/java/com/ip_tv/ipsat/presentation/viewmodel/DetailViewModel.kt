package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.SearchResults
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.domain.model.VodMovieResponse
import com.ip_tv.ipsat.domain.repository.DetailRepository
import com.ip_tv.ipsat.domain.repository.HomeRepository
import com.ip_tv.ipsat.utils.hasConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo: DetailRepository,
    private val repository: HomeRepository
) : ViewModel() {
    private val _seriesDetailResponse: MutableLiveData<Resource<SeriesDetailResponse>> =
        MutableLiveData()

    val seriesDetailResponse: MutableLiveData<Resource<SeriesDetailResponse>> =
        _seriesDetailResponse

    private val _movieDetailResponse: MutableLiveData<Resource<VodMovieResponse>> =
        MutableLiveData()

    val movieDetailResponse: MutableLiveData<Resource<VodMovieResponse>> = _movieDetailResponse


    private val _searchResult: MutableLiveData<Resource<ArrayList<Movie>>> = MutableLiveData()
    val searchResult: MutableLiveData<Resource<ArrayList<Movie>>> = _searchResult

    fun getSearchResult(query: String, year: String) {
        _searchResult.postValue(Resource.Loading)
        viewModelScope.launch {
            repository.filterMovies(
                SearchResults(
                    hasNextPage = false,
                    page = 1,
                    rating = query,
                    releaseYear = year.toInt(),
                    results = arrayListOf()
                )
            ).onEach {
                it.onSuccess {
                    _searchResult.postValue(Resource.Success(it.results as ArrayList<Movie>))
                }
                it.onFailure {
                    _searchResult.postValue(Resource.Error(java.lang.Exception(it.message.toString())))
                }
            }.launchIn(viewModelScope)
        }
    }

    fun loadDetail(id: Int) {
        _seriesDetailResponse.value = Resource.Loading
        if (hasConnection()) {
            repo.getSeriesContent(id).onEach { result ->
                result.onFailure {
                    _seriesDetailResponse.postValue(Resource.Error(Exception(it.message)))
                }
                result.onSuccess { series ->
                        _seriesDetailResponse.postValue(Resource.Success(series))

                }
            }.launchIn(viewModelScope)
        } else {
            _seriesDetailResponse.postValue(Resource.Error(Exception("No internet connection !")))
        }
    }

    suspend fun checkMovieSeries(query: Int, movie: Movie): Boolean {
        return repository.checkMovieOrSeries(query)
    }

    fun loadMovieVod(id: Int) {
        _movieDetailResponse.value = Resource.Loading
        if (hasConnection()) {
            repo.getMovieResponse(id).onEach {
                it.onFailure {
                    _movieDetailResponse.postValue(Resource.Error(Exception(it.message)))
                }
                it.onSuccess {
                    _movieDetailResponse.postValue(Resource.Success(it))
                }
            }.launchIn(viewModelScope)
        } else {
            _movieDetailResponse.postValue(Resource.Error(Exception("No internet connection !")))
        }
    }


//    // ✅ Vod Listni olish
//    private val _vodList = MutableLiveData<Resource<List<VodMovieResponse>>>()
//    val vodList: LiveData<Resource<List<VodMovieResponse>>> get() = _vodList
//
//    fun fetchSeriesVodList(list: List<Item0>) = flow {
//        val resultList = arrayListOf<VodMovieResponse>()
//        _vodList.postValue(Resource.Loading) // Loading statusini boshlash
//
//        list.forEach { item ->
//            try {
//                repo.getSeriesVod(item.id)
//                    .catch { e -> Log.e("SeriesViewModel", "Error: ${e.message}") }
//                    .collect { result ->
//                        if (result.isSuccess) {
//                            result.getOrNull()?.let {
//                                resultList.add(it)
//                            }
//                        }
//                    }
//            } catch (e: Exception) {
//                Log.e("fetchSeriesVodList", "Error fetching series vod: ${e.message}")
//            }
//        }
//
//        emit(resultList) //
//    }

}