package com.ip_tv.ipsat.domain.usecase

import com.ip_tv.ipsat.data.repository.AuthRepositoryImpl
import javax.inject.Inject

class CheckSubscribeUseCase @Inject constructor(private val repo:AuthRepositoryImpl) {
    fun checkSubscribe() = repo.subscriptionDetails()
}