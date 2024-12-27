package com.ip_tv.ipsat.domain.usecase

import com.ip_tv.ipsat.data.repository.HomeRepositoryImpl
import javax.inject.Inject

class MovieScreenUse @Inject constructor(private val  repo: HomeRepositoryImpl) {
    fun getBannerData() = repo.loadBanner()
}