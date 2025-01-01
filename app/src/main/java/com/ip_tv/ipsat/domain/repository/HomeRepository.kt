package com.ip_tv.ipsat.domain.repository

import androidx.paging.PagingSource
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.SearchResults
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun loadBanner(): Flow<Result<ArrayList<Movie>>>
    suspend fun getMovies(page: Int): Flow<Result<ArrayList<Movie>>>

    suspend fun getSeries(page: Int): Flow<Result<ArrayList<Movie>>>

    suspend fun getDocuments(page: Int): Flow<Result<ArrayList<Movie>>>

    suspend fun getKids(page: Int): Flow<Result<ArrayList<Movie>>>

    suspend fun filterMovies(results: SearchResults) :Flow<Result<SearchResults>>
    suspend fun filterDocumentary(results: SearchResults) :Flow<Result<SearchResults>>
    suspend fun filterKids(results: SearchResults) :Flow<Result<SearchResults>>
    suspend fun filterSeries(results: SearchResults) :Flow<Result<SearchResults>>
}