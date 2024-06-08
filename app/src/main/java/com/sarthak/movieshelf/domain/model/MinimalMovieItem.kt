package com.sarthak.movieshelf.domain.model

data class MinimalMovieItem (
    val id: Int,
    val posterPath: String,
    val title: String,
    val releaseDate: String
)

fun MinimalMovieItem.getReleaseYear(): String {
    return if(releaseDate.isNotBlank()) {
        releaseDate.slice(0..3)
    }
    else ""
}