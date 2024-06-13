package com.sarthak.movieshelf.domain.model

data class Review(
    val userId: String = "",
    val movieId: Int = 0,
    val rating: Double = 0.0,
    val reviewText: String = "",
    val viewingDateInMillis: Long? = null,
)
