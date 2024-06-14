package com.sarthak.movieshelf.ui.movieDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.data.remote.api.TmdbApi.Companion.API_KEY
import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.domain.model.Review
import com.sarthak.movieshelf.domain.repository.MovieRepository
import com.sarthak.movieshelf.service.AuthService
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
    private val authService: AuthService,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val movieId = checkNotNull(savedStateHandle.get<Int>("id"))
    private val _state = MutableStateFlow(MovieDetailsState())
    val state: StateFlow<MovieDetailsState> = _state
    private val _movieShelfDataState = MutableStateFlow(MovieShelfDataState())
    val movieShelfDataState: StateFlow<MovieShelfDataState> = _movieShelfDataState
    private val _watchlistState = MutableStateFlow(WatchlistState())
    val watchlistState: StateFlow<WatchlistState> = _watchlistState
    init {
        viewModelScope.launch(Dispatchers.IO) {
            refreshFireStoreData()
            getIsMovieInWatchlist()
            getMovieById()
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
            fireStoreService.getRatingAverageAndCount(movieId).collect {fetchResult ->
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
    private suspend fun getMovieById() {
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

    fun updateWatchList() {
        viewModelScope.launch(Dispatchers.IO) {
            authService.getCurrentUser()?.let {
                fireStoreService.updateWatchlist(it.uid, movieId).collect{fetchResult ->
                    when(fetchResult) {
                        is FetchResult.Loading -> {
                            _watchlistState.value = _watchlistState.value.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = ""
                            )
                        }
                        is FetchResult.Error -> {
                            _watchlistState.value = _watchlistState.value.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = fetchResult.message
                            )
                        }
                        is FetchResult.Success -> {
                            fetchResult.data?.let {
                                _watchlistState.value = _watchlistState.value.copy(
                                    isLoading = false,
                                    isError = false,
                                    isMovieInWatchlist = !_watchlistState.value.isMovieInWatchlist!!,
                                    errorMessage = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getIsMovieInWatchlist() {
        viewModelScope.launch((Dispatchers.IO)) {
            authService.getCurrentUser()?.let {
                fireStoreService.isMovieInWatchlist(it.uid, movieId).collect{fetchResult ->
                    when(fetchResult) {
                        is FetchResult.Loading -> {
                            _watchlistState.value = _watchlistState.value.copy(
                                isMovieInWatchlist = null
                            )
                        }
                        is FetchResult.Error -> {
                            _watchlistState.value = _watchlistState.value.copy(
                                isMovieInWatchlist = null
                            )
                        }
                        is FetchResult.Success -> {
                            fetchResult.data?.let {
                                _watchlistState.value = _watchlistState.value.copy(
                                    isMovieInWatchlist = fetchResult.data
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}



data class MovieDetailsState(
    val movieItem: MovieItem = MovieItem(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val reviewsWithUsername: List<Pair<Review, String>> = emptyList(),
)

data class MovieShelfDataState(
    val movieShelfRatingAverage: Float = -1.0F,
    val movieShelfRatingCount: Int = -1,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)

data class WatchlistState(
    val isMovieInWatchlist: Boolean? = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
)