package com.mihailloh.imdb.data

import com.mihailloh.imdb.models.Movie
import com.mihailloh.imdb.models.Review
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val moviesRef = db.collection("movies")

    fun getMoviesCollection() = moviesRef

    fun getMovieDoc(movieId: String) = moviesRef.document(movieId)

    fun addReviewAndUpdateStats(movieId: String, review: Review) =
        db.runTransaction { transaction ->
            val movieDoc = moviesRef.document(movieId)
            val snap = transaction.get(movieDoc)

            val currentAvg = snap.getDouble("avgRating") ?: 0.0
            val currentCount = (snap.getLong("reviewCount") ?: 0L)
            val newCount = currentCount + 1
            val newAvg = (currentAvg * currentCount + review.rating) / newCount

            val reviewRef = movieDoc.collection("reviews").document()
            transaction.set(reviewRef, mapOf(
                "userId" to review.userId,
                "userName" to review.userName,
                "rating" to review.rating,
                "text" to review.text,
                "timestamp" to (review.timestamp ?: Timestamp.now())
            ))
            transaction.set(movieDoc, mapOf(
                "avgRating" to newAvg,
                "reviewCount" to newCount
            ), SetOptions.merge())
            null
        }

    fun getReviewsCollection(movieId: String) = moviesRef.document(movieId).collection("reviews")
}