package com.sarthak.movieshelf.ui.movieDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.data.remote.api.TmdbApi.Companion.API_KEY
import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.domain.repository.MovieRepository
import com.sarthak.movieshelf.utils.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Error
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val movieId = checkNotNull(savedStateHandle.get<Int>("id"))
    private val _state = MutableStateFlow(MovieDetailsState())
    val state: StateFlow<MovieDetailsState> = _state
    init {
        viewModelScope.launch {
            getMovieById(movieId)
        }
    }

    private suspend fun getMovieById(movieId: Int) {
        viewModelScope.launch {
            getMovieDetailsById()
        }
    }

    private suspend fun getMovieDetailsById() {
        viewModelScope.launch {

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
                        fetchResult.data.let {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                isError = false,
                                errorMessage = "",
                                movieItem = fetchResult.data!!
                            )
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
    val errorMessage: String = ""
)