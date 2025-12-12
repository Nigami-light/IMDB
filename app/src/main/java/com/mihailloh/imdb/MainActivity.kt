package com.mihailloh.imdb

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mihailloh.imdb.models.Movie
import com.mihailloh.imdb.ui.MoviesAdapter
import com.mihailloh.imdb.data.FirestoreRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var rvMovies: RecyclerView
    private lateinit var adapter: MoviesAdapter
    private val repo = FirestoreRepository()
    private val auth = FirebaseAuth.getInstance()

    private var moviesListenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth.signInAnonymously().addOnCompleteListener {
        }

        rvMovies = findViewById(R.id.rvMovies)
        adapter = MoviesAdapter(onClick = { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("movieId", movie.id)
            startActivity(intent)
        })
        rvMovies.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddMovie).setOnClickListener {

        }

        moviesListenerRegistration = repo.getMoviesCollection()
            .orderBy("title", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    // можно показать Toast или лог
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val list = snapshots.documents.map { doc ->
                        Movie(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            year = (doc.getLong("year") ?: 0L).toInt(),
                            description = doc.getString("description") ?: "",
                            posterUrl = doc.getString("posterUrl"),
                            avgRating = doc.getDouble("avgRating") ?: 0.0,
                            reviewCount = doc.getLong("reviewCount") ?: 0L
                        )
                    }
                    adapter.setData(list)
                }
            }
    }

    override fun onDestroy() {
        moviesListenerRegistration?.remove()
        super.onDestroy()
    }
}