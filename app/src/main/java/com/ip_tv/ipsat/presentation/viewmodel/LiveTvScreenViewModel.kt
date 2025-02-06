package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.data.repository.LiveTvRepositoryImpl
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.ChannelResponse
import com.ip_tv.ipsat.domain.model.SubCategory
import com.ip_tv.ipsat.domain.usecase.LiveTvScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LiveTvScreenViewModel @Inject constructor(
    private val liveTvUseCase: LiveTvScreenUseCase, private val repo: LiveTvRepositoryImpl
) : ViewModel() {
    private val _tvCategoryState = MutableStateFlow<Resource<ChannelCategory>>(Resource.Idle)
    val tvCategory get() = _tvCategoryState

    private val _subCategoryData = MutableStateFlow<Resource<SubCategory>>(Resource.Idle)
    val subCategoryData get() = _subCategoryData
    private val _channelData = MutableLiveData<Resource<ChannelResponse>>(Resource.Idle)

    val channelsData get() = _channelData


    fun loadSubCategory() {
        _subCategoryData.value = Resource.Loading
        repo.loadAllSubCategory().onEach {
            it.onSuccess {
                _subCategoryData.value = Resource.Success(it)
            }
            it.onFailure {
                _subCategoryData.value = Resource.Error(Exception(it.message))
            }

        }.launchIn(viewModelScope)

    }

    fun loadChannelsByCategory(id: Int) {
        _channelData.value = Resource.Loading
        repo.loadChannelByCategory(id).onEach {
            it.onSuccess {
                Log.d("TAG", "loadChannelsByCategory:${it} ")
                _channelData.postValue(Resource.Success(it))
            }
            it.onFailure {
                _channelData.value = Resource.Error(Exception(it.message))
            }
        }.launchIn(viewModelScope)
    }

    fun loadCategory() {
        _tvCategoryState.value = Resource.Loading
        liveTvUseCase.loadCategory().onEach {
            it.onSuccess {
                _tvCategoryState.value = Resource.Success(it)
            }
            it.onFailure {
                _tvCategoryState.value = Resource.Error(Exception(it.message))
            }
        }.launchIn(viewModelScope)
    }
}