package com.sarthak.movieshelf.data.mappers

import com.sarthak.movieshelf.data.remote.response.CastDto
import com.sarthak.movieshelf.data.remote.response.CrewDto
import com.sarthak.movieshelf.data.remote.response.MinimalMovieItemDto
import com.sarthak.movieshelf.data.remote.response.MovieItemDto
import com.sarthak.movieshelf.data.remote.response.MovieListResponseDto
import com.sarthak.movieshelf.data.remote.response.VideoItemDto
import com.sarthak.movieshelf.domain.model.Cast
import com.sarthak.movieshelf.domain.model.Crew
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.domain.model.MovieListResponseItem
import com.sarthak.movieshelf.domain.model.VideoItem

fun MinimalMovieItemDto.toMinimalMovieItem() = MinimalMovieItem(
    id = id,
    posterPath = poster_path ?: "",
    title = title ?: "",
    releaseDate = release_date ?: ""
)

fun MovieListResponseDto.toMovieListResponseItem() = MovieListResponseItem(
    page = page,
    totalPages = if(total_pages > 500) {
        500
    } else total_pages,
    moviesList = results.map {
        it.toMinimalMovieItem()
    }
)

fun VideoItemDto.toVideoItem() = VideoItem(
    key = key ?: "",
    official = official ?: false,
    site = site ?: "",
    type = type ?: "",
    name = name ?: ""
)

fun CastDto.toCast() = Cast(
    character = character ?: "",
    id = id ?: -1,
    name = name ?: "",
    profilePath = profile_path ?: ""
)

fun CrewDto.toCrew() = Crew(
    id = id ?: -1,
    job = job ?: "",
    name = name ?: "",
    profilePath = profile_path ?: ""
)

fun MovieItemDto.toMovieItem() = MovieItem(
    id = id ?: -1,
    backdropPath = backdrop_path ?: "",
    title = title ?: "",
    releaseDate = release_date ?: "",
    runtime = runtime ?: -1,
    videos = videos?.let {
        videos.results?.let {
            it.map {videoItemDto ->
                videoItemDto.toVideoItem()
            }
        }
    } ?: emptyList(),
    posterPath = poster_path ?: "",
    tagline = tagline ?: "",
    overview = overview ?: "",
    voteAverage = vote_average ?: -1.0,
    voteCount = vote_count ?: -1,
    cast = credits?.let {
        credits.cast?.let {
            it.map {castDto ->
                castDto.toCast()
            }
        }
    } ?: emptyList(),
    crew = credits?.let {
        credits.crew?.let {
            it.map {crewDto ->
                crewDto.toCrew()
            }
        }
    } ?: emptyList(),
    genres = genres?.let {
        genres.map {genreItemDto ->
            genreItemDto.name
        }
    } ?: emptyList(),
    originCountry = origin_country ?: emptyList(),
    originalLanguage = original_language ?: "",
    productionCompanies = production_companies?.let {
        production_companies.map {productionCompanyItemDto ->
            productionCompanyItemDto.name
        }
    } ?: emptyList(),
    spokenLanguages = spoken_languages?.let {
        spoken_languages.map {
            it.name
        }
    } ?: emptyList()
)