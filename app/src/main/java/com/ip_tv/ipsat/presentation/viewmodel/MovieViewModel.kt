package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.usecase.MovieScreenUse
import com.ip_tv.ipsat.utils.hasConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val movieScreenUse: MovieScreenUse) : ViewModel() {

    private val _initBanner = MutableStateFlow<Resource<ArrayList<Movie>>>(Resource.Idle)
    val initBanner get() = _initBanner
    private var isDataLoaded = false

    fun loadBanner() {
        if (isDataLoaded) return

        if (hasConnection()) {
            _initBanner.value = Resource.Loading
            movieScreenUse.getBannerData()
                .onEach { result ->
                    result.onSuccess { data ->
                        _initBanner.value = Resource.Success(data)
                        isDataLoaded = true
                    }
                    result.onFailure { exception ->
                        _initBanner.value = Resource.Error(Exception(exception.message))
                    }
                }
                .launchIn(viewModelScope)
        } else {
            _initBanner.value = Resource.Error(Exception("No internet connection"))
        }
    }

    fun resetData() {
        isDataLoaded = false
        _initBanner.value = Resource.Idle
    }
}
