package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import com.ip_tv.ipsat.domain.usecase.CheckSubscribeUseCase
import com.ip_tv.ipsat.domain.usecase.LoginUseCase
import com.ip_tv.ipsat.utils.hasConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(private val useCase:CheckSubscribeUseCase) :ViewModel() {
    private var _userDetail :MutableLiveData<Resource<SubscriptionResponse>> =MutableLiveData()
    val userDetail :LiveData<Resource<SubscriptionResponse>> get() = _userDetail

    fun getUserDetail(){
        if (hasConnection()){
            _userDetail.value = Resource.Loading
            useCase.checkSubscribe().onEach {
                it.onFailure {
                    _userDetail.value = Resource.Error(Exception(it.message))
                }
                it.onSuccess {
                    _userDetail.value = Resource.Success(it)
                }
            }.launchIn(viewModelScope)
        }else {
            _userDetail.value = Resource.Error(Exception("No internet connection"))
        }
    }
}