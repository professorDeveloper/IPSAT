package com.ip_tv.ipsat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.model.SearchResults
import com.ip_tv.ipsat.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowMoreMovieViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    val result: MutableLiveData<SearchResults?> = MutableLiveData()
    var searched = false
    var notSet = true
    lateinit var searchResults: SearchResults

    fun loadSearch(r: SearchResults) {
        viewModelScope.launch {
            repository.filterMovies(r).onEach {
                it.onSuccess {
                    Log.d("TAG", "loadSearch: ${it.results}")
                    result.postValue(it)
                }
                it.onFailure {

                }
            }.launchIn(viewModelScope)
        }
    }

    fun loadNextPage(r: SearchResults) {
        val data = r.copy(page = r.page + 1)
        viewModelScope.launch {
            repository.filterMovies(data).onEach {
                it.onFailure {

                }

                it.onSuccess {
                    result.postValue(it)
                }
            }.launchIn(viewModelScope)
        }
    }


}