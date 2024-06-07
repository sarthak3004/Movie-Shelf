package com.sarthak.movieshelf.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.sarthak.movieshelf.data.remote.response.CreditsDto
import com.sarthak.movieshelf.data.remote.response.VideosResponseDto
import java.time.LocalDateTime

data class MovieItem(
    val id: Int = -1,
    val backdropPath: String = "",
    val title: String = "",
    val releaseDate: String = "",
    val runtime: Int = -1,
    val videos: List<VideoItem> = emptyList(),
    val posterPath: String = "",
    val tagline: String = "",
    val overview: String = "",
    val voteAverage: Double = -1.0,
    val voteCount: Int = -1,
    val cast: List<Cast> = emptyList(),
    val crew: List<Crew> = emptyList(),
    val genres: List<String> = emptyList(),
    val originCountry: List<String> = emptyList(),
    val originalLanguage: String = "",
    val productionCompanies: List<String> = emptyList(),
    val spokenLanguages: List<String> = emptyList(),
)

fun MovieItem.getDirectorsList(): List<String>  {
    var directorList = emptyList<String>()
    crew.map {
        if(it.job == "Director") {
            directorList = directorList.plus(it.name)
        }
    }
    return directorList
}

fun MovieItem.getReleaseYear(): String {
    return if(releaseDate.isNotBlank()) {
        releaseDate.slice(0..3)
    }
    else ""
}

fun MovieItem.getYoutubeVideoItems(): List<VideoItem> {
    var youtubeVideoItems = emptyList<VideoItem>()
    if(videos.isNotEmpty()) {
        videos.map {
            if(it.site == "YouTube") {
                youtubeVideoItems = youtubeVideoItems.plus(it)
            }
        }
    }
    return youtubeVideoItems
}