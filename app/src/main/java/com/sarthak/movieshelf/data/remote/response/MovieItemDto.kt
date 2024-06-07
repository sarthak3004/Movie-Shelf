package com.sarthak.movieshelf.data.remote.response

data class MovieItemDto(
    val id: Int?,
    val backdrop_path: String?,
    val title: String?,
    val release_date: String?,
    val runtime: Int?,
    val videos: VideosResponseDto?,
    val poster_path: String?,
    val tagline: String?,
    val overview: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    val credits: CreditsDto?,
    val genres: List<GenreItemDto>?,
    val origin_country: List<String>?,
    val original_language: String?,
    val production_companies: List<ProductionCompanyItemDto>?,
    val spoken_languages: List<SpokenLanguageItemDto>?,
)