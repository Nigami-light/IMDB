package com.mihailloh.imdb.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mihailloh.imdb.R
import com.mihailloh.imdb.models.Review

class ReviewsAdapter(
    private val items: MutableList<Review> = mutableListOf()
) : RecyclerView.Adapter<ReviewsAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvUser: TextView = view.findViewById(R.id.tvUser)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvText: TextView = view.findViewById(R.id.tvText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = items[position]
        holder.tvUser.text = r.userName
        holder.tvRating.text = "★ ${r.rating}"
        holder.tvText.text = r.text
    }

    override fun getItemCount(): Int = items.size

    fun setData(list: List<Review>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}