package com.sarthak.movieshelf.ui.basicSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.data.remote.api.TmdbApi.Companion.API_KEY
import com.sarthak.movieshelf.domain.model.MovieListResponseItem
import com.sarthak.movieshelf.domain.repository.MovieRepository
import com.sarthak.movieshelf.utils.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BasicSearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository
): ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _state = MutableStateFlow(BasicSearchState())
    val state: StateFlow<BasicSearchState> = _state

    private var searchJob: Job? = null

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
        _state.value = BasicSearchState()
        if(newQuery.isNotBlank()) {
            viewModelScope.launch {
                getSearchSuggestions()
            }
        }
    }

    private fun getSearchSuggestions() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val page = if(_state.value.movieListResponseItem.page > 0) {
                _state.value.movieListResponseItem.page
            } else 1
            movieRepository.getMoviesListByQuery(_searchQuery.value, page, API_KEY).collect {fetchResult ->
                when(fetchResult) {
                    is FetchResult.Error -> {
                        _state.value = _state.value.copy(
                            isError = true,
                            isLoading = false,
                            movieListResponseItem = MovieListResponseItem()
                        )
                    }
                    is FetchResult.Loading -> {
                        _state.value = _state.value.copy(
                            isError = false,
                            isLoading = true
                        )
                    }
                    is FetchResult.Success -> {
                        fetchResult.data?.let {
                            var tempMovieList = _state.value.movieListResponseItem.moviesList
                            tempMovieList = tempMovieList.plus(fetchResult.data.moviesList)

                            _state.value = _state.value.copy(
                                isError = false,
                                isLoading = false,
                                movieListResponseItem = fetchResult.data.copy(
                                    moviesList = tempMovieList
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        if(_state.value.movieListResponseItem.page < _state.value.movieListResponseItem.totalPages) {
            _state.value = _state.value.copy(
                movieListResponseItem = _state.value.movieListResponseItem.copy(
                    page = _state.value.movieListResponseItem.page + 1
                )
            )
            getSearchSuggestions()
        }
    }
}

data class BasicSearchState(
    val movieListResponseItem: MovieListResponseItem = MovieListResponseItem(),
    var isLoading: Boolean = false,
    var isError: Boolean = false,
)
