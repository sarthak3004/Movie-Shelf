package com.sarthak.movieshelf.domain.model

data class MovieListResponseItem(
    val page: Int = -1,
    val moviesList: List<MinimalMovieItem> = emptyList(),
    val totalPages: Int = -1
)
