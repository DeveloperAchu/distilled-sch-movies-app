package com.developerachu.moviesapp.models

/**
 * Data class to represent a tv show with the properties
 * [id]
 * [name],
 * [imageUrl],
 * [popularity],
 * [averageVote]
 */
data class TvShow(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val popularity: String,
    val averageVote: String
)