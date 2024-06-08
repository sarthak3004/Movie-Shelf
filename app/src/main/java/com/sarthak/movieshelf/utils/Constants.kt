package com.sarthak.movieshelf.utils

const val BASE_URL = "https://api.themoviedb.org/3/"
const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

enum class MOVIES_LIST_TYPE(val listType: String) {
    TRENDING("Trending"),
    UPCOMING("Upcoming"),
    TOP_RATED("Top Rated")
}