package com.ip_tv.ipsat.domain.usecase

import com.ip_tv.ipsat.data.repository.AuthRepositoryImpl
import com.ip_tv.ipsat.data.repository.HomeRepositoryImpl
import javax.inject.Inject

class NotificationUseCase @Inject constructor(private val repo:AuthRepositoryImpl) {

    fun getMessages() = repo.getMessages()
}