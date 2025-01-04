package com.ip_tv.ipsat.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.databinding.ItemMovieBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.setAnimation

class MovieAdapter(private val activity:Fragment) : RecyclerView.Adapter<MovieAdapter.MovieVh>() {

    private val movieList = mutableListOf<Movie>()
    private lateinit var clickListener : (Movie) -> Unit

    fun setItemClickListener(listener: (Movie) -> Unit) {
        clickListener = listener
    }

   inner class MovieVh(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(movie: Movie) {
            setAnimation(activity.requireActivity(), binding.root)
            binding.titleItem.text = movie.name ?: "Unknown"
            binding.itemCompactScore.text = movie.rating?.toString() ?: "No Rating"
            binding.itemImg.loadImage(movie.image ?: "")
            binding.root.setOnClickListener {
                clickListener.invoke(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVh {
        return MovieVh(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MovieVh, position: Int) {
        val movie = movieList[position]
        holder.onBind(movie)
    }

    override fun getItemCount(): Int = movieList.size

    fun submitListNew(newMovies: List<Movie>) {
        movieList.clear()
        movieList.addAll(newMovies)
        notifyDataSetChanged()
    }

    fun submitList(newMovies: List<Movie>) {
        val startPosition = movieList.size
        movieList.addAll(newMovies)
        notifyItemRangeInserted(startPosition, newMovies.size)
    }
}
