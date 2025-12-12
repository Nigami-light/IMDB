package com.mihailloh.imdb.models

data class Movie(
    val id: String = "",
    val title: String = "",
    val year: Int = 0,
    val description: String = "",
    val posterUrl: String? = null,
    val avgRating: Double = 0.0,
    val reviewCount: Long = 0L
)