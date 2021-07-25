package com.developerachu.moviesapp.models

/**
 * Data class to represent a tv show with the properties
 * [id]
 * [name],
 * [imageUrl],
 * [popularityString],
 * [popularityValue]
 * [averageVote],
 * [firstAirDateString],
 * [firstAirDateLongValue]
 */
data class TvShow(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val popularityString: String,
    val popularityValue: Double,
    val averageVote: Number,
    val firstAirDateString: String,
    val firstAirDateLongValue: Long
)