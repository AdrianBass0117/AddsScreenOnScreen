package com.aldrinlewys117.addspantallaenpantalla

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RowAdapter(private val rows: List<MovieRow>) : RecyclerView.Adapter<RowAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvRowTitle)
        val posters: RecyclerView = view.findViewById(R.id.rvPosters)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        val vh = VH(v)
        vh.posters.layoutManager =
            LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val row = rows[position]
        holder.title.text = row.title
        holder.posters.adapter = PosterAdapter(row.movies)
    }

    override fun getItemCount() = rows.size
}

class PosterAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<PosterAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivPoster)
        val title: TextView = view.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_poster, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val movie = movies[position]
        holder.title.text = movie.title
        Glide.with(holder.image).load(movie.imageUrl).into(holder.image)
        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, movie.title, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = movies.size
}
