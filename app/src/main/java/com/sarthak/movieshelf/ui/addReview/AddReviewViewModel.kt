package com.sarthak.movieshelf.ui.addReview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.service.AuthService
import com.sarthak.movieshelf.service.FireStoreService
import com.sarthak.movieshelf.utils.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReviewViewModel @Inject constructor(
    private val authService: AuthService,
    private val fireStoreService: FireStoreService,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = MutableStateFlow(AddReviewState())
    val state: StateFlow<AddReviewState> = _state

    init {
        val movieId = savedStateHandle.get<Int>("id") ?: -1
        val posterUrl = savedStateHandle.get<String>("posterPath") ?: ""
        val title = savedStateHandle.get<String>("title") ?: ""
        val releaseDate = savedStateHandle.get<String>("releaseDate") ?: ""
        _state.value = _state.value.copy(
            minimalMovieItem = MinimalMovieItem(movieId, posterUrl, title, releaseDate)
        )
    }


    fun onDateChange(newValue: Long) {
        _state.value = _state.value.copy(dateInMillis = newValue)
    }

    fun onRatingChange(newValue: Float) {
        _state.value = _state.value.copy(rating = newValue)
    }

    fun onReviewChange(newValue: String) {
        _state.value = _state.value.copy(review = newValue)
    }

    fun sendReview(navigate: () -> Boolean) {
        viewModelScope.launch {
            try {
                authService.getCurrentUser()?.let {
                    fireStoreService.addReview(
                        userId = it.uid,
                        movieId = _state.value.minimalMovieItem.id,
                        posterPath = _state.value.minimalMovieItem.posterPath,
                        title = _state.value.minimalMovieItem.title,
                        releaseDate = _state.value.minimalMovieItem.releaseDate,
                        reviewText = _state.value.review,
                        ratingValue = _state.value.rating,
                        viewingDate = if(_state.value.dateInMillis.toInt() == -1) null else _state.value.dateInMillis
                    ).collect{fetchResult ->
                        when(fetchResult) {
                            is FetchResult.Error -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    isError = true,
                                    errorMessage = fetchResult.message
                                )
                            }
                            is FetchResult.Loading -> {
                                _state.value = _state.value.copy(
                                    isLoading = true,
                                    isError = false,
                                    errorMessage = ""
                                )
                            }
                            is FetchResult.Success -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    isError = false,
                                    errorMessage = "",
                                )
                                navigate()
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



data class AddReviewState(
    val rating: Float = 2.5F,
    val dateInMillis: Long = -1,
    var review: String = "",
    val minimalMovieItem: MinimalMovieItem = MinimalMovieItem(-1,"", "", ""),
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var errorMessage: String = ""
)
