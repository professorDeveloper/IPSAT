package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.usecase.LiveTvScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LiveTvScreenViewModel @Inject constructor(private val liveTvUseCase: LiveTvScreenUseCase) :
    ViewModel() {
    private val _tvCategoryState = MutableStateFlow<Resource<ChannelCategory>>(Resource.Idle)
    val tvCategory get() = _tvCategoryState


    fun loadSubCategory(){

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