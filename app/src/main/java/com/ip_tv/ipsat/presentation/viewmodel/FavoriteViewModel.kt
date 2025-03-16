/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.data.local.entity.MovieBookmark
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.repository.HomeRepository
import com.ip_tv.ipsat.domain.repository.MovieBookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    val repo: MovieBookmarkRepository,
    private val repoHome: HomeRepository
) : ViewModel() {
    private var _favoriteMovieResponse = MutableLiveData<ArrayList<Movie>>()
    val favoriteMovieResponse get() = _favoriteMovieResponse

    suspend fun checkMovieSeries(query: Int,movie: Movie) :Boolean{
        return repoHome.checkMovieOrSeries(query)
    }
    fun getFavoriteMovies() {
//        _favoriteMovieResponse.value = repo.getAllBookmarks()
        viewModelScope.launch {
            val localList = repo.getAllBookmarks()
            val movieList = localList.map {
                Movie(
                    categoryProperty = it.categoryProperty,
                    country = it.country.toString(),
                    description = it.description,
                    id = it.id,
                    image = it.image,
                    name = it.title,
                    rating = it.rating,
                    release_year = it.release_year,
                    language = it.language.toString(),
                    categoryid = it.categoryid.toString(),
                )
            }
            _favoriteMovieResponse.postValue(movieList as ArrayList<Movie>)

        }
    }
}