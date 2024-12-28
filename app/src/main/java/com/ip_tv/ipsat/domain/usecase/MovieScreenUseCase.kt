package com.ip_tv.ipsat.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ip_tv.ipsat.data.repository.HomeRepositoryImpl
import com.ip_tv.ipsat.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieScreenUseCase @Inject constructor(private val  repo: HomeRepositoryImpl) {
    fun getBannerData() = repo.loadBanner()
   suspend fun getMovies(page:Int) = repo.getMovies(page)
    suspend fun getSeries(page:Int) = repo.getSeries(page)
    suspend fun getDocumentary(page:Int) = repo.getDocuments(page)
}