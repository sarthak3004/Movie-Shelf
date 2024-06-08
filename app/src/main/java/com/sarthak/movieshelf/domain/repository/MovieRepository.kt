package com.sarthak.movieshelf.domain.repository

import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.domain.model.MovieListResponseItem
import com.sarthak.movieshelf.utils.FetchResult
import com.sarthak.movieshelf.utils.MOVIES_LIST_TYPE
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getMoviesList(
        type: MOVIES_LIST_TYPE,
        apiKey: String,
        page: Int = 1
    ): Flow<FetchResult<MovieListResponseItem>>

    suspend fun getMovieById(
        id: String,
        appendToResponse: String,
        apiKey: String
    ): Flow<FetchResult<MovieItem>>

    suspend fun getMoviesListByQuery(
        query: String,
        page: Int,
        apiKey: String
    ): Flow<FetchResult<MovieListResponseItem>>
}