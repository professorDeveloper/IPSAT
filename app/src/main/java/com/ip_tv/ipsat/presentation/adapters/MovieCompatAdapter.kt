package com.ip_tv.ipsat.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ItemMovieBinding
import com.ip_tv.ipsat.databinding.ItemMovieCompatBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.setAnimation
import jp.wasabeef.glide.transformations.BlurTransformation

class MovieCompatAdapter (private val movieList:ArrayList<Movie>) : RecyclerView.Adapter<MovieCompatAdapter.MovieVh>() {
    lateinit var itemClickListener : (Movie) -> Unit

    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        itemClickListener = listener
    }

   inner class MovieVh(private val binding: ItemMovieCompatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(movie: Movie) {
            binding.itemCompactImage.loadImage(movie.image)
            setAnimation(binding.root.context, binding.root)
            binding.itemCompactImage.loadImage(movie.image)
            Glide.with(binding.root.context as Context)
                .load(GlideUrl(movie.image ))
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(400)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(2, 3)))
                .into(binding.itemCompactBanner)
            binding.itemCompactTitle.text = movie.name
            binding.itemCompactScore.text = movie.rating.toString()
            binding.itemTotal.text=movie.language+" • "+movie.release_year+" • "+movie.country
            binding.itemContainer.setOnClickListener {
                itemClickListener.invoke(movie)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVh {
        return MovieVh(
            ItemMovieCompatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieVh, position: Int) {
        val movie = movieList[position]
        holder.onBind(movie)
    }

    override fun getItemCount(): Int = movieList.size

    fun submitList(newMovies: List<Movie>) {
        val startPosition = movieList.size
        movieList.addAll(newMovies)
        notifyItemRangeInserted(startPosition, newMovies.size)
    }

    fun submitNewList(newMovies: List<Movie>) {
        movieList.clear()
        movieList.addAll(newMovies)
        notifyDataSetChanged()
    }
}
