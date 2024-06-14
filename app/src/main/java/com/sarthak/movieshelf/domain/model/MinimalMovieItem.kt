package com.sarthak.movieshelf.domain.model

import com.google.firebase.Timestamp

data class MinimalMovieItem (
    val id: Int = -1,
    val posterPath: String = "",
    val title: String = "",
    val releaseDate: String = "",
)

fun MinimalMovieItem.getReleaseYear(): String {
    return if(releaseDate.isNotBlank()) {
        releaseDate.slice(0..3)
    }
    else ""
}