package com.sarthak.movieshelf.service

import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.domain.model.Review
import com.sarthak.movieshelf.domain.model.User
import com.sarthak.movieshelf.utils.FetchResult
import kotlinx.coroutines.flow.Flow

interface FireStoreService {

    suspend fun addUser(userId: String, user: User)
    suspend fun isUsernameExists(username: String): Boolean

    suspend fun getUsername(userId: String): String
    suspend fun addReview(
        userId: String,
        movieId: Int,
        title: String,
        posterPath: String,
        releaseDate: String,
        reviewText: String,
        ratingValue: Float,
        viewingDate: Long? = null
    ) : Flow<FetchResult<String>>

    suspend fun getRatingAverageAndCount(movieId: Int): Flow<FetchResult<Pair<Float, Int>>>

    suspend fun getReviews(movieId: Int): Flow<FetchResult<List<Pair<Review,String>>>>

    suspend fun isMovieInWatchlist(userId: String, movieId: Int): Flow<FetchResult<Boolean>>
    suspend fun updateWatchlist(
        userId: String,
        movieId: Int,
        posterPath: Any,
        title: Any,
        releaseDate: Any
    ): Flow<FetchResult<Boolean>>
    suspend fun getMoviesInWatchlist(userId: String): Flow<FetchResult<List<MinimalMovieItem>>>
}