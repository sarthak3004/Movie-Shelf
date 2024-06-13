package com.sarthak.movieshelf.domain.model

data class User(
    val username: String = "",
    val watchlist: List<MinimalMovieItem> = emptyList(),
)
