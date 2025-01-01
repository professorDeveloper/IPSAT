package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    private val _searchResult: MutableLiveData<Resource<ArrayList<Movie>>> = MutableLiveData()
    val searchResult: MutableLiveData<Resource<ArrayList<Movie>>> = _searchResult
    var hasNextPage = false
    fun getSearchResult(query: String) {
        _searchResult.postValue(Resource.Loading)
        viewModelScope.launch {
            repository.search(query).onEach {
                it.onSuccess {
                    _searchResult.postValue(Resource.Success(it.results as ArrayList<Movie>))
                }
                it.onFailure {
                    _searchResult.postValue(Resource.Error(Exception(it.message.toString())))
                }
            }.launchIn(viewModelScope)
        }
    }
}