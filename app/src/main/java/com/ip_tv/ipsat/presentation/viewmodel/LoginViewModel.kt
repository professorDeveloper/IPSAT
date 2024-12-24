package com.ip_tv.ipsat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.domain.usecase.LoginUseCase
import com.ip_tv.ipsat.utils.AuthState
import com.zbekz.tashkentmetro.utils.hasConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val useCase: LoginUseCase):ViewModel() {
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState = _loginState

    fun activateLogin(code: String, macAddress: String) {
        _loginState.value = AuthState.Loading
       if (hasConnection()) {
           useCase.activateLogin(code, macAddress).onEach {
               it.onSuccess {
                   _loginState.value =AuthState.Verified(it)
               }
               it.onFailure {
                   _loginState.value = AuthState.Error(it.message.toString())
               }
           }.launchIn(viewModelScope)
       }else {
           _loginState.value = AuthState.Error("No internet connection")
       }
    }
}