package com.sarthak.movieshelf.ui.movieDetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.data.remote.api.TmdbApi.Companion.API_KEY
import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.domain.model.Review
import com.sarthak.movieshelf.domain.repository.MovieRepository
import com.sarthak.movieshelf.service.FireStoreService
import com.sarthak.movieshelf.utils.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val fireStoreService: FireStoreService,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val movieId = checkNotNull(savedStateHandle.get<Int>("id"))
    private val _state = MutableStateFlow(MovieDetailsState())
    val state: StateFlow<MovieDetailsState> = _state
    private val _movieShelfDataState = MutableStateFlow(MovieShelfDataState())
    val movieShelfDataState: StateFlow<MovieShelfDataState> = _movieShelfDataState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            refreshFireStoreData()
            getMovieById(movieId)
        }
    }

    private fun getReviewsWithUsername() {
        viewModelScope.launch((Dispatchers.IO)) {
            fireStoreService.getReviews(movieId).collect{fetchResult ->
                when(fetchResult) {
                    is FetchResult.Loading -> {
                        _state.value = _state.value.copy(
                            reviewsWithUsername = emptyList()
                        )
                    }
                    is FetchResult.Error -> {
                        _state.value = _state.value.copy(
                            reviewsWithUsername = emptyList()
                        )
                    }
                    is FetchResult.Success -> {
                        fetchResult.data?.let {
                            _state.value = _state.value.copy(
                                reviewsWithUsername = fetchResult.data
                            )
                        }
                    }
                }

            }
        }
    }

    private fun getMovieShelfRatingAverageAndCount() {
        viewModelScope.launch((Dispatchers.IO)) {
            fireStoreService.getRatingAverageAndCount(movieId).collect() {fetchResult ->
                when(fetchResult) {
                    is FetchResult.Error -> {
                        _movieShelfDataState.value = _movieShelfDataState.value.copy(
                            isError = true,
                            isLoading = false,
                            errorMessage = fetchResult.message,
                            movieShelfRatingAverage = -1.0F,
                            movieShelfRatingCount = -1,
                        )
                    }
                    is FetchResult.Loading -> {
                        _movieShelfDataState.value = _movieShelfDataState.value.copy(
                            isLoading = true,
                            isError = false,
                            errorMessage = "",
                            movieShelfRatingAverage = -1.0F,
                            movieShelfRatingCount = -1,
                        )
                    }
                    is FetchResult.Success -> {
                        fetchResult.data?.let {
                            _movieShelfDataState.value = _movieShelfDataState.value.copy(
                                isLoading = false,
                                isError = false,
                                errorMessage = "",
                                movieShelfRatingAverage = fetchResult.data.first,
                                movieShelfRatingCount = fetchResult.data.second,
                            )
                        }
                    }
                }
            }
        }
    }
    private suspend fun getMovieById(movieId: Int) {
        viewModelScope.launch((Dispatchers.IO)) {
            getMovieDetailsById()
        }
    }

    private suspend fun getMovieDetailsById() {
        viewModelScope.launch((Dispatchers.IO)) {

            movieRepository.getMovieById(
                id = movieId.toString(),
                appendToResponse = "credits,videos",
                apiKey = API_KEY
            ).collect {fetchResult ->
                when(fetchResult) {
                    is FetchResult.Error -> {
                        _state.value = _state.value.copy(
                            isError = true,
                            isLoading = false,
                            errorMessage = fetchResult.message,
                            movieItem = MovieItem()
                        )
                    }
                    is FetchResult.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            isError = false,
                            errorMessage = "",
                            movieItem = MovieItem()
                        )
                    }
                    is FetchResult.Success -> {
                        fetchResult.data?.let {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                isError = false,
                                errorMessage = "",
                                movieItem = fetchResult.data
                            )
                        }
                    }
                }
            }
        }

    }

    fun refreshFireStoreData() {
        viewModelScope.launch(Dispatchers.IO) {
            getMovieShelfRatingAverageAndCount()
            getReviewsWithUsername()
        }
    }
}



data class MovieDetailsState(
    val movieItem: MovieItem = MovieItem(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val reviewsWithUsername: List<Pair<Review, String>> = emptyList()
)

data class MovieShelfDataState(
    val movieShelfRatingAverage: Float = -1.0F,
    val movieShelfRatingCount: Int = -1,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)