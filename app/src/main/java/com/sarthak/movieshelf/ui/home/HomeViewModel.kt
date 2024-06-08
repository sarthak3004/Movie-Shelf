package com.sarthak.movieshelf.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.data.remote.api.TmdbApi.Companion.API_KEY
import com.sarthak.movieshelf.domain.model.MovieListResponseItem
import com.sarthak.movieshelf.domain.repository.MovieRepository
import com.sarthak.movieshelf.utils.FetchResult
import com.sarthak.movieshelf.utils.MOVIES_LIST_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
): ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state
    init {
        viewModelScope.launch {
            getTrendingMoviesForWeek()
            getUpcomingMovies()
            getTopRatedMovies()
        }
    }

    private suspend fun getTrendingMoviesForWeek() {
        viewModelScope.launch {

            movieRepository.getMoviesList(MOVIES_LIST_TYPE.TRENDING, API_KEY).collect { fetchResult ->
                when(fetchResult) {
                    is FetchResult.Error -> {
                        _state.value = _state.value.copy(
                            trendingState = _state.value.trendingState.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = fetchResult.message,
                                moviesResponse = MovieListResponseItem()
                            )
                        )
                    }
                    is FetchResult.Loading -> {
                        _state.value = _state.value.copy(
                            trendingState = _state.value.trendingState.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = "",
                                moviesResponse = MovieListResponseItem()
                            )
                        )
                    }
                    is FetchResult.Success -> {
                        fetchResult.data.let {
                            _state.value = _state.value.copy(
                                trendingState = _state.value.trendingState.copy(
                                    isLoading = false,
                                    isError = false,
                                    errorMessage = "",
                                    moviesResponse = fetchResult.data!!
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getTopRatedMovies() {
        viewModelScope.launch {

            movieRepository.getMoviesList(MOVIES_LIST_TYPE.TOP_RATED, API_KEY).collect { fetchResult ->
                when(fetchResult) {
                    is FetchResult.Error -> {
                        _state.value = _state.value.copy(
                            topRatedState = _state.value.topRatedState.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = fetchResult.message,
                                moviesResponse = MovieListResponseItem()
                            )
                        )
                    }
                    is FetchResult.Loading -> {
                        _state.value = _state.value.copy(
                            topRatedState = _state.value.topRatedState.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = "",
                                moviesResponse = MovieListResponseItem()
                            )
                        )
                    }
                    is FetchResult.Success -> {
                        fetchResult.data.let {
                            _state.value = _state.value.copy(
                                topRatedState = _state.value.topRatedState.copy(
                                    isLoading = false,
                                    isError = false,
                                    errorMessage = "",
                                    moviesResponse = fetchResult.data!!
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getUpcomingMovies() {
        viewModelScope.launch {

            movieRepository.getMoviesList(MOVIES_LIST_TYPE.UPCOMING, API_KEY).collect { fetchResult ->
                when(fetchResult) {
                    is FetchResult.Error -> {
                        _state.value = _state.value.copy(
                            upcomingState = _state.value.upcomingState.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = fetchResult.message,
                                moviesResponse = MovieListResponseItem()
                            )
                        )
                    }
                    is FetchResult.Loading -> {
                        _state.value = _state.value.copy(
                            upcomingState = _state.value.upcomingState.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = "",
                                moviesResponse = MovieListResponseItem()
                            )
                        )
                    }
                    is FetchResult.Success -> {
                        fetchResult.data.let {
                            _state.value = _state.value.copy(
                                upcomingState = _state.value.upcomingState.copy(
                                    isLoading = false,
                                    isError = false,
                                    errorMessage = "",
                                    moviesResponse = fetchResult.data!!
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}



data class HomeState(
    var trendingState: MoviesListState = MoviesListState(),
    var upcomingState: MoviesListState = MoviesListState(),
    var topRatedState: MoviesListState = MoviesListState(),
)

data class MoviesListState(
    var moviesResponse: MovieListResponseItem = MovieListResponseItem(),
    var isError: Boolean = false,
    var isLoading: Boolean = false,
    var errorMessage: String = ""
)