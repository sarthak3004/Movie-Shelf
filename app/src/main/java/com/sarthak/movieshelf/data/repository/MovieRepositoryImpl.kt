package com.sarthak.movieshelf.data.repository

import com.sarthak.movieshelf.data.mappers.toMinimalMovieItem
import com.sarthak.movieshelf.data.mappers.toMovieItem
import com.sarthak.movieshelf.data.remote.api.TmdbApi
import com.sarthak.movieshelf.domain.repository.MovieRepository
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.utils.FetchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val tmdbApi: TmdbApi
): MovieRepository {

    override suspend fun getTrendingMoviesForWeek(apiKey: String): Flow<FetchResult<List<MinimalMovieItem>>> = flow {
        emit(FetchResult.Loading())
        val trendingMoviesResponseDto = try {
            tmdbApi.getTrendingMovies(apiKey)
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

        trendingMoviesResponseDto.let {
            val trendingMovies = it.results.map { minimalMovieItemDto ->
                minimalMovieItemDto.toMinimalMovieItem()
            }
            emit(FetchResult.Success(data = trendingMovies))
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
}