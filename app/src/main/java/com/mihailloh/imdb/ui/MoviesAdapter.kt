package com.mihailloh.imdb.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mihailloh.imdb.R
import com.mihailloh.imdb.models.Movie

class MoviesAdapter(
    private val items: MutableList<Movie> = mutableListOf(),
    private val onClick: (Movie) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val imgPoster: ImageView = view.findViewById(R.id.imgPoster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val movie = items[position]
        holder.tvTitle.text = movie.title
        holder.tvYear.text = movie.year.takeIf { it != 0 }?.toString() ?: ""
        holder.tvRating.text = "★ ${"%.1f".format(movie.avgRating)} (${movie.reviewCount})"

        if (!movie.posterUrl.isNullOrBlank()) {
            try {
            } catch (t: Throwable) {
                holder.imgPoster.setImageResource(R.drawable.ic_movie_placeholder)
            }
        } else {
            holder.imgPoster.setImageResource(R.drawable.ic_movie_placeholder)
        }

        holder.itemView.setOnClickListener { onClick(movie) }
    }

    override fun getItemCount(): Int = items.size

    fun setData(list: List<Movie>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
