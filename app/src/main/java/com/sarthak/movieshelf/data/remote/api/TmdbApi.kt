package com.sarthak.movieshelf.data.remote.api

import com.sarthak.movieshelf.BuildConfig
import com.sarthak.movieshelf.data.remote.response.MovieItemDto
import com.sarthak.movieshelf.data.remote.response.TrendingMoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String
    ): TrendingMoviesResponseDto

//    https://api.themoviedb.org/3/movie/614933?append_to_response=credits%2Cvideos
    @GET("movie/{movieId}")
    suspend fun getMovieById(
        @Path("movieId") movieId: String,
        @Query("append_to_response") responseAdditions: String,
        @Query("api_key") apiKey: String
    ): MovieItemDto

    companion object {
        const val API_KEY = BuildConfig.api_key
    }
}