package com.sarthak.movieshelf.data.remote.api

import com.sarthak.movieshelf.BuildConfig
import com.sarthak.movieshelf.data.remote.response.MovieItemDto
import com.sarthak.movieshelf.data.remote.response.MovieListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieListResponseDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieListResponseDto

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieListResponseDto

    @GET("movie/{movieId}")
    suspend fun getMovieById(
        @Path("movieId") movieId: String,
        @Query("append_to_response") responseAdditions: String,
        @Query("api_key") apiKey: String
    ): MovieItemDto

    //https://api.themoviedb.org/3/search/movie
    @GET("search/movie")
    suspend fun getMovieListByQuery(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): MovieListResponseDto

    companion object {
        const val API_KEY = BuildConfig.api_key
    }
}