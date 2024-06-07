package com.sarthak.movieshelf.domain.repository

import com.sarthak.movieshelf.data.remote.response.MovieItemDto
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.utils.FetchResult
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getTrendingMoviesForWeek(
        apiKey: String
    ): Flow<FetchResult<List<MinimalMovieItem>>>

    suspend fun getMovieById(
        id: String,
        appendToResponse: String,
        apiKey: String
    ): Flow<FetchResult<MovieItem>>
}