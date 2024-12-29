package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.usecase.MovieScreenUseCase
import com.ip_tv.ipsat.utils.hasConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MovieViewModel @Inject constructor(private val movieScreenUse: MovieScreenUseCase) : ViewModel() {

    private val _initBanner = MutableStateFlow<Resource<ArrayList<Movie>>>(Resource.Idle)
    val initBanner get() = _initBanner
    private var page = 1
    private var randomPage = 1
    private var random =1
    private var isDataLoaded = false

    private val _movies = MutableLiveData<Resource<ArrayList<Movie>>>(Resource.Idle)
    val movies: LiveData<Resource<ArrayList<Movie>>> get() = _movies



    private val _series = MutableLiveData<Resource<ArrayList<Movie>>>(Resource.Idle)
    val series: LiveData<Resource<ArrayList<Movie>>> get() = _series

    private val  _documentary = MutableLiveData<Resource<ArrayList<Movie>>>(Resource.Idle)
    val documentary: LiveData<Resource<ArrayList<Movie>>> get() = _documentary

    private val  _randomMovies = MutableLiveData<Resource<ArrayList<Movie>>>(Resource.Idle)
    val randomMovies: LiveData<Resource<ArrayList<Movie>>> get() = _randomMovies

    private val _nextRandomMovies = MutableLiveData<Resource<ArrayList<Movie>>>(Resource.Idle)
    val nextRandomMovies: LiveData<Resource<ArrayList<Movie>>> get() = _nextRandomMovies

    fun loadNextPage()
    {
        if (isDataLoaded) return
        if (hasConnection()) {
          _movies.postValue(Resource.Loading)
        viewModelScope.launch {
            movieScreenUse.getMovies(page)
                .onEach { result ->
                    result.onSuccess { data ->
                        isDataLoaded = true
                        data.shuffle()
                        _movies.value = Resource.Success(data)
                    }
                    result.onFailure { exception ->
                        _movies.value = Resource.Error(Exception(exception.message))
                    }
                }
                .launchIn(viewModelScope)
        }
      }else {
          _movies.postValue(Resource.Error(Exception("No internet connection !")))
      }
    }


    fun loadNextRandomPage() {
        randomPage+=1
         if (hasConnection()) {
             _nextRandomMovies.postValue(Resource.Loading)
             viewModelScope.launch {
                 when(random){
                     1->{
                         movieScreenUse.getMovies(page=randomPage.toString().toInt()).onEach { result ->
                             result.onSuccess { data ->
                                 if(randomPage==1) data.shuffle()
                                 _nextRandomMovies.value = Resource.Success(data)
                             }
                             result.onFailure { exception ->
                                 _nextRandomMovies.value = Resource.Error(Exception(exception.message))
                             }
                         }.launchIn(viewModelScope)
                     }
                     2->{
                         movieScreenUse.getSeries(page=randomPage.toString().toInt()).onEach { result ->
                             result.onSuccess { data ->
                                 if(randomPage==1) data.shuffle()
                                 _nextRandomMovies.value = Resource.Success(data)
                             }
                             result.onFailure { exception ->
                                 _nextRandomMovies.value = Resource.Error(Exception(exception.message))
                             }
                         }.launchIn(viewModelScope)
                     }
                     3->{
                         movieScreenUse.getDocumentary(page=randomPage.toString().toInt()).onEach { result ->
                             result.onSuccess { data ->
                                 if(randomPage==1) data.shuffle()
                                 _nextRandomMovies.value = Resource.Success(data)
                             }
                             result.onFailure { exception ->
                                 _nextRandomMovies.value = Resource.Error(Exception(exception.message))
                             }
                         }.launchIn(viewModelScope)
                     }
                     else->{
                         movieScreenUse.getMovies(page=randomPage.toString().toInt()).onEach { result ->
                             result.onSuccess { data ->
                                 if(randomPage==1) data.shuffle()
                                 _nextRandomMovies.value = Resource.Success(data)
                             }
                             result.onFailure { exception ->
                                 _nextRandomMovies.value = Resource.Error(Exception(exception.message))
                             }
                         }.launchIn(viewModelScope)
                     }
                 }
             }
         }else {
             _nextRandomMovies.postValue(Resource.Error(Exception("No internet connection !")))
         }
    }


    fun loadSeries() {
        if (isDataLoaded) return
        if (hasConnection()) {
            _series.postValue(Resource.Loading)
            viewModelScope.launch {
                movieScreenUse.getSeries(page)
                    .onEach { result ->
                        result.onSuccess { data ->
                            data.shuffle()
                            isDataLoaded = true
                            _series.value = Resource.Success(data)
                        }
                        result.onFailure { exception ->
                            _series.value = Resource.Error(Exception(exception.message))
                        }
                    }
                    .launchIn(viewModelScope)
            }
        } else {
            _series.postValue(Resource.Error(Exception("No internet connection !")))
        }
    }

    fun loadBanner() {
        if (isDataLoaded) return

        if (hasConnection()) {
            _initBanner.value = Resource.Loading
            movieScreenUse.getBannerData()
                .onEach { result ->
                    result.onSuccess { data ->
                        data.shuffle()
                        _initBanner.value = Resource.Success(data)
                        isDataLoaded = true
                    }
                    result.onFailure { exception ->
                        _initBanner.value = Resource.Error(Exception(exception.message))
                    }
                }
                .launchIn(viewModelScope)
        } else {
            _initBanner.value = Resource.Error(Exception("No internet connection"))
        }
    }

    fun loadDocumentary(){
        if (isDataLoaded) return
        if (hasConnection()) {
            _documentary.postValue(Resource.Loading)
            viewModelScope.launch {
                movieScreenUse.getDocumentary(page)
                    .onEach { result ->
                        result.onSuccess { data ->
                            isDataLoaded = true
                            data.shuffle()
                            _documentary.value = Resource.Success(data)
                        }
                        result.onFailure { exception ->
                            _documentary.value = Resource.Error(Exception(exception.message))
                        }
                    }
                    .launchIn(viewModelScope)
            }
        } else {
            _documentary.postValue(Resource.Error(Exception("No internet connection !")))
        }
    }

    fun loadRandomData() {
      if (isDataLoaded) return
        viewModelScope.launch {
            when (random) {
                1 -> {
                    movieScreenUse.getMovies(page=randomPage.toString().toInt()).onEach { result ->
                        result.onSuccess { data ->
                            data.shuffle()
                            _randomMovies.value = Resource.Success(data)
                        }
                        result.onFailure { exception ->
                            _randomMovies.value = Resource.Error(Exception(exception.message))
                        }

                    }.launchIn(viewModelScope)
                }
                2 -> {
                    movieScreenUse.getSeries(page=randomPage.toString().toInt()).onEach { result ->
                        result.onSuccess { data ->
                            data.shuffle()
                            _randomMovies.value = Resource.Success(data)
                        }
                        result.onFailure { exception ->
                            _randomMovies.value = Resource.Error(Exception(exception.message))
                        }
                    }.launchIn(viewModelScope)
                }
                3 -> {
                    movieScreenUse.getDocumentary(page=randomPage.toString().toInt()).onEach { result ->
                        result.onSuccess { data ->
                            data.shuffle()
                            _randomMovies.value = Resource.Success(data)
                        }
                        result.onFailure { exception ->
                            _randomMovies.value = Resource.Error(Exception(exception.message))
                        }
                    }.launchIn(viewModelScope)
                }
                4 -> {
                    movieScreenUse.getDocumentary(page=randomPage.toString().toInt()).onEach { result ->
                        result.onSuccess { data ->
                            data.shuffle()
                            _randomMovies.value = Resource.Success(data)
                        }
                        result.onFailure { exception ->
                            _randomMovies.value = Resource.Error(Exception(exception.message))
                        }
                    }.launchIn(viewModelScope)
                }
            }

        }
    }

    fun resetData() {
        isDataLoaded = false
        _initBanner.value = Resource.Idle
        _movies.value = Resource.Idle
        _series.value = Resource.Idle

    }
}
