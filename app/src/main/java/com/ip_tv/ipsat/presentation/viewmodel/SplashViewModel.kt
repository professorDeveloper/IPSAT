package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.domain.usecase.CheckSubscribeUseCase
import com.ip_tv.ipsat.utils.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SplashViewModel  @Inject constructor(private val useCase:CheckSubscribeUseCase,private val userPreferenceManager: UserPreferenceManager):ViewModel(){
    private val _initSplash = MutableStateFlow<Resource<SubscriptionResponse>>(Resource.Idle)
    private val _isFirst =MutableLiveData<Unit>()
    val initSplash = _initSplash
    val isFirst = _isFirst

    fun checkSubscribe(){
        if(userPreferenceManager.isLogged) {
            _initSplash.value = Resource.Loading
            useCase.checkSubscribe().onEach {
                it.onFailure {
                    _initSplash.value = Resource.Error(
                        Exception(it.message)
                    )
                }

                it.onSuccess {
                    _initSplash.value = Resource.Success(it)
                }
            }.launchIn(viewModelScope)
        }else {
            isFirst.postValue(Unit)
        }
    }

}