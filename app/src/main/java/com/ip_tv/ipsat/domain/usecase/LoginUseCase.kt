package com.ip_tv.ipsat.domain.usecase

import com.ip_tv.ipsat.data.repository.AuthRepositoryImpl
import javax.inject.Inject


class LoginUseCase @Inject constructor(private val repo:AuthRepositoryImpl) {

    fun activateLogin(code: String, macAddress: String) = repo.activateCode(code, macAddress)
}