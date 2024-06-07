package com.sarthak.movieshelf.data.remote.response

data class CreditsDto(
    val cast: List<CastDto>?,
    val crew: List<CrewDto>?
)