package com.sarthak.movieshelf.domain.model

data class FireStoreMovieItem(
    val minimalMovieItem: MinimalMovieItem = MinimalMovieItem(
        id = -1,
        posterPath = "",
        title = "",
        releaseDate = ""
    ),
    val averageRating: Double = 0.0,
    val ratingCount: Int = 0
)
