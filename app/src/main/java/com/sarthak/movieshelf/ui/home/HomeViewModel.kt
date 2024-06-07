package com.sarthak.movieshelf.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.data.remote.api.TmdbApi.Companion.API_KEY
import com.sarthak.movieshelf.domain.repository.MovieRepository
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.utils.FetchResult
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
        Log.d("HOMEVIEWMODEL", API_KEY)
        viewModelScope.launch {
            getTrendingMoviesForWeek()
        }
    }

    private suspend fun getTrendingMoviesForWeek() {
        viewModelScope.launch {

            movieRepository.getTrendingMoviesForWeek(API_KEY).collect {fetchResult ->
                when(fetchResult) {
                    is FetchResult.Error -> {
                        _state.value = _state.value.copy(
                            trendingState = _state.value.trendingState.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = fetchResult.message,
                                trendingMovies = emptyList()
                            )
                        )
                    }
                    is FetchResult.Loading -> {
                        _state.value = _state.value.copy(
                            trendingState = _state.value.trendingState.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = "",
                                trendingMovies = emptyList()
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
                                    trendingMovies = fetchResult.data!!
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
    var trendingState: TrendingState = TrendingState()
)

data class TrendingState(
    var trendingMovies: List<MinimalMovieItem> = emptyList(),
    var isError: Boolean = false,
    var isLoading: Boolean = false,
    var errorMessage: String = ""
)