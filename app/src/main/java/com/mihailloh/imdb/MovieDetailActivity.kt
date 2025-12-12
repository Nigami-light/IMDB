package com.mihailloh.imdb

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mihailloh.imdb.data.FirestoreRepository
import com.mihailloh.imdb.models.Review
import com.mihailloh.imdb.ui.ReviewsAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MovieDetailActivity : AppCompatActivity() {

    private val repo = FirestoreRepository()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var tvTitle: TextView
    private lateinit var tvAvgRating: TextView
    private lateinit var tvDesc: TextView
    private lateinit var rvReviews: RecyclerView
    private lateinit var btnAddReview: Button
    private lateinit var adapter: ReviewsAdapter

    private var movieId: String? = null
    private var movieListener: com.google.firebase.firestore.ListenerRegistration? = null
    private var reviewsListener: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        movieId = intent.getStringExtra("movieId")
        if (movieId == null) finish()

        tvTitle = findViewById(R.id.tvMovieTitle)
        tvAvgRating = findViewById(R.id.tvMovieAvgRating)
        tvDesc = findViewById(R.id.tvDescription)
        rvReviews = findViewById(R.id.rvReviews)
        btnAddReview = findViewById(R.id.btnAddReview)
        adapter = ReviewsAdapter()
        rvReviews.adapter = adapter

        movieListener = repo.getMovieDoc(movieId!!)
            .addSnapshotListener { snap, _ ->
                if (snap != null && snap.exists()) {
                    tvTitle.text = snap.getString("title") ?: ""
                    val avg = snap.getDouble("avgRating") ?: 0.0
                    val count = snap.getLong("reviewCount") ?: 0L
                    tvAvgRating.text = "★ ${"%.1f".format(avg)} ($count reviews)"
                    tvDesc.text = snap.getString("description") ?: ""
                }
            }

        reviewsListener = repo.getReviewsCollection(movieId!!)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snaps, _ ->
                if (snaps != null) {
                    val list = snaps.documents.map { d ->
                        Review(
                            id = d.id,
                            userId = d.getString("userId") ?: "",
                            userName = d.getString("userName") ?: "Anonymous",
                            rating = (d.getLong("rating") ?: 0L).toInt(),
                            text = d.getString("text") ?: "",
                            timestamp = d.getTimestamp("timestamp")
                        )
                    }
                    adapter.setData(list)
                }
            }

        btnAddReview.setOnClickListener {
            showAddReviewDialog()
        }
    }

    private fun showAddReviewDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_review, null)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val etReview = view.findViewById<EditText>(R.id.etReviewText)

        AlertDialog.Builder(this)
            .setTitle("Add review")
            .setView(view)
            .setPositiveButton("Post") { dialog, _ ->
                val ratingInt = ratingBar.rating.toInt().coerceIn(1, 5)
                val text = etReview.text.toString().trim()
                val user = auth.currentUser
                if (user == null) {
                    auth.signInAnonymously().addOnCompleteListener { task ->
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        postReview(uid, "Anon", ratingInt, text)
                    }
                } else {
                    val uid = user.uid
                    val name = user.displayName ?: "User"
                    postReview(uid, name, ratingInt, text)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun postReview(userId: String, userName: String, rating: Int, text: String) {
        val review = Review(
            userId = userId,
            userName = userName,
            rating = rating,
            text = text,
            timestamp = Timestamp.now()
        )
        repo.addReviewAndUpdateStats(movieId!!, review)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
            }
    }

    override fun onDestroy() {
        movieListener?.remove()
        reviewsListener?.remove()
        super.onDestroy()
    }
}