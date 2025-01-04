package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.domain.repository.DetailRepository
import com.ip_tv.ipsat.utils.hasConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repo:DetailRepository):ViewModel() {
    private val  _seriesDetailResponse :MutableLiveData<Resource<SeriesDetailResponse>> = MutableLiveData()

    val seriesDetailResponse:MutableLiveData<Resource<SeriesDetailResponse>> = _seriesDetailResponse

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
}