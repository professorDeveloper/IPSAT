package com.ip_tv.ipsat.presentation.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.DetailScreenBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.toReadableDateTime
import com.ip_tv.ipsat.utils.toYear

class DetailScreen : BaseFragment<DetailScreenBinding>(DetailScreenBinding::inflate) {
    override fun onViewCreate(savedInstanceState: Bundle?) {
        val movie = requireArguments().getSerializable("movie") as Movie
        loadData(movie)
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(movie: Movie) {
        binding.ivBackdrop.loadImage(movie.image)
        binding.tvMovieTitleValue.text = movie.name

        binding.yearValue.text = movie.release_year
        binding.durationValue.text = movie.country
        binding.itemImg.loadImage(movie.image)
        binding.tvDescriptionValue.text =
            "Movie Rating :${movie.rating} - Movie Language: ${movie.language.toUpperCase()} ${if (movie.categoryProperty != null) "\n Movie Category :${movie.categoryProperty}" else ""} \nMovie Description :" + movie.description
        binding.countryValue.text = movie.rating.toString()

    }
}