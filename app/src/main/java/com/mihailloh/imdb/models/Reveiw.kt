package com.mihailloh.imdb.models

import com.google.firebase.Timestamp

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val text: String = "",
    val timestamp: Timestamp? = null
)