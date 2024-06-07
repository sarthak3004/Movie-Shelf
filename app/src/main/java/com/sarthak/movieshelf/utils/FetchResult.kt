package com.sarthak.movieshelf.utils

sealed class FetchResult<T>(
    val data: T? = null,
    val message: String = ""
) {
    class Success<T>(data: T?) : FetchResult<T>(data)
    class Error<T>(message: String, data: T? = null) : FetchResult<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true) : FetchResult<T>(null)
}