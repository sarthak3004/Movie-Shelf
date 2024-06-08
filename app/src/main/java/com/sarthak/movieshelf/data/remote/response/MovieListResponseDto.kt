package com.sarthak.movieshelf.data.remote.response

data class MovieListResponseDto (
    val page: Int,
    val results: List<MinimalMovieItemDto>,
    val total_pages: Int
)