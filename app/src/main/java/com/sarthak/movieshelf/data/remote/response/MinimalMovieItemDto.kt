package com.sarthak.movieshelf.data.remote.response

data class MinimalMovieItemDto(
    val id: Int,
    val poster_path: String?,
    val title: String?,
    val release_date: String?
)

