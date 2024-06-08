package com.sarthak.movieshelf.data.repository

import com.sarthak.movieshelf.data.mappers.toMovieItem
import com.sarthak.movieshelf.data.mappers.toMovieListResponseItem
import com.sarthak.movieshelf.data.remote.api.TmdbApi
import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.domain.model.MovieListResponseItem
import com.sarthak.movieshelf.domain.repository.MovieRepository
import com.sarthak.movieshelf.utils.FetchResult
import com.sarthak.movieshelf.utils.MOVIES_LIST_TYPE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val tmdbApi: TmdbApi
): MovieRepository {

    override suspend fun getMoviesList(
        type: MOVIES_LIST_TYPE,
        apiKey: String,
        page: Int
    ): Flow<FetchResult<MovieListResponseItem>> = flow {
        emit(FetchResult.Loading())
        val moviesListResponseDto = try {
            when(type) {
                MOVIES_LIST_TYPE.TRENDING -> tmdbApi.getTrendingMovies(apiKey, page)
                MOVIES_LIST_TYPE.TOP_RATED -> tmdbApi.getTopRatedMovies(apiKey, page)
                MOVIES_LIST_TYPE.UPCOMING -> tmdbApi.getUpcomingMovies(apiKey, page)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        }

        moviesListResponseDto.let {
            val moviesResponse = moviesListResponseDto.toMovieListResponseItem()
            emit(FetchResult.Success(data = moviesResponse))
        }
    }

    override suspend fun getMovieById(
        id: String,
        appendToResponse: String,
        apiKey: String
    ): Flow<FetchResult<MovieItem>> = flow {
        emit(FetchResult.Loading())
        val movieByIdResponse = try {
            tmdbApi.getMovieById(
                movieId = id,
                responseAdditions = appendToResponse,
                apiKey = apiKey
            )
        } catch (e: IOException) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        }

        movieByIdResponse.let {
            val movieDetails = it.toMovieItem()
            emit(FetchResult.Success(data = movieDetails))
        }
    }

    override suspend fun getMoviesListByQuery(
        query: String,
        page: Int,
        apiKey: String
    ): Flow<FetchResult<MovieListResponseItem>> = flow {
        emit(FetchResult.Loading())
        val moviesListResponseDto = try {
            tmdbApi.getMovieListByQuery(
                query = query,
                page = page,
                apiKey = apiKey
            )
        } catch (e: IOException) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(message = "Oh! Could not load data."))
            return@flow
        }

        moviesListResponseDto.let {
            val moviesResponse = moviesListResponseDto.toMovieListResponseItem()
            emit(FetchResult.Success(data = moviesResponse))
        }
    }
}