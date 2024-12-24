package com.ip_tv.ipsat.utils

import com.ip_tv.ipsat.domain.model.LoginResponse

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Verified(val response: LoginResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}
