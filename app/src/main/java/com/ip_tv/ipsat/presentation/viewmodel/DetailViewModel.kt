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
class DetailViewModel @Inject constructor(private val repo:DetailRepository,private val repository:HomeRepository):ViewModel() {
    private val  _seriesDetailResponse :MutableLiveData<Resource<SeriesDetailResponse>> = MutableLiveData()

    val seriesDetailResponse:MutableLiveData<Resource<SeriesDetailResponse>> = _seriesDetailResponse

    private val  _movieDetailResponse :MutableLiveData<Resource<VodMovieResponse>> = MutableLiveData()

    val movieDetailResponse:MutableLiveData<Resource<VodMovieResponse>> = _movieDetailResponse

    private val _searchResult: MutableLiveData<Resource<ArrayList<Movie>>> = MutableLiveData()
    val searchResult: MutableLiveData<Resource<ArrayList<Movie>>> = _searchResult

    fun getSearchResult(query: String,year:String) {
        _searchResult.postValue(Resource.Loading)
        viewModelScope.launch {
            repository.filterMovies(SearchResults(hasNextPage = false, page = 1, rating =query, releaseYear = year.toInt(), results = arrayListOf())).onEach {
                it.onSuccess {
                    _searchResult.postValue(Resource.Success(it.results as ArrayList<Movie>))
                }
                it.onFailure {
                    _searchResult.postValue(Resource.Error(java.lang.Exception(it.message.toString())))
                }
            }.launchIn(viewModelScope)
        }
    }

    fun loadDetail(id:Int){
        _seriesDetailResponse.value = Resource.Loading
        if (hasConnection()) {
            repo.getSeriesContent(id).onEach {
                it.onFailure {
                    _seriesDetailResponse.postValue(Resource.Error(Exception(it.message)))
                }
                it.onSuccess {
                    _seriesDetailResponse.postValue(Resource.Success(it))
                }

            }.launchIn(viewModelScope)
        }else {
            _seriesDetailResponse.postValue(Resource.Error(Exception("No internet connection !")))
        }
    }

    fun loadMovieVod(id:Int) {
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
        }else {
            _movieDetailResponse.postValue(Resource.Error(Exception("No internet connection !")))
        }
    }
}