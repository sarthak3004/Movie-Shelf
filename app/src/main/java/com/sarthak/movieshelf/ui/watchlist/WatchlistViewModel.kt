package com.sarthak.movieshelf.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
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
class WatchlistViewModel @Inject constructor(
    private val authService: AuthService,
    private val fireStoreService: FireStoreService,
): ViewModel() {

    private val _state = MutableStateFlow(WatchlistDataState())
    val state: StateFlow<WatchlistDataState> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getWatchlistMovies()
        }
    }

    private fun getWatchlistMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authService.getCurrentUser()?.let {
                    fireStoreService.getMoviesInWatchlist(it.uid).collect {fetchResult ->
                        when(fetchResult) {
                            is FetchResult.Loading -> {
                                _state.value = _state.value.copy(
                                    isError = false,
                                    isLoading = true,
                                    errorMessage = "",
                                    minimalMoviesItemList = emptyList()
                                )
                            }
                            is FetchResult.Error -> {
                                _state.value = _state.value.copy(
                                    isError = true,
                                    isLoading = false,
                                    errorMessage = fetchResult.message,
                                    minimalMoviesItemList = emptyList()
                                )
                            }
                            is FetchResult.Success -> {
                                fetchResult.data?.let {
                                    _state.value = _state.value.copy(
                                        isError = false,
                                        isLoading = false,
                                        errorMessage = "",
                                        minimalMoviesItemList = fetchResult.data
                                    )
                                }
                            }
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}



data class WatchlistDataState(
    val minimalMoviesItemList: List<MinimalMovieItem> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)
