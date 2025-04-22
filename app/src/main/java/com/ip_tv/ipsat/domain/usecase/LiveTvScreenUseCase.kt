package com.ip_tv.ipsat.domain.usecase

import com.ip_tv.ipsat.data.repository.LiveTvRepositoryImpl
import javax.inject.Inject

class LiveTvScreenUseCase @Inject constructor(private val repo: LiveTvRepositoryImpl) {
    fun loadCategory() = repo.getLiveTvCategories()
    fun loadSubCategory() = repo.loadAllSubCategory()
    fun loadChannelByCategory(categoryId: Int) = repo.loadChannelByCategory(categoryId)
    fun getEventChannels() = repo.getEventChannels()
}