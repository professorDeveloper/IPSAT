package com.ip_tv.ipsat.presentation.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.DetailScreenBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.presentation.adapters.MovieAdapter
import com.ip_tv.ipsat.presentation.viewmodel.SearchViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.toReadableDateTime
import com.ip_tv.ipsat.utils.toYear
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailScreen : BaseFragment<DetailScreenBinding>(DetailScreenBinding::inflate) {

    private val model by viewModels<SearchViewModel>()

    override fun onViewCreate(savedInstanceState: Bundle?) {
        val movie = requireArguments().getSerializable("movie") as Movie
        val query =if (movie.name.length>4) movie.name.substring(0,4) else movie.name
        model.getSearchResult(query)
        loadData(movie)
        observeData()
    }

    private fun observeData () {
        model.searchResult.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    val adapter = MovieAdapter(this)
                    adapter.submitList(it.data)
                    binding.similarMoviesRecycler.adapter=adapter
                }
                is Resource.Error -> {
                    showSnack(binding.root, it.throwable.message.toString())
                }
                is Resource.Loading -> {

                }
                else -> {}
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun loadData(movie: Movie) {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivPoster.loadImage(movie.image)
        binding.backgroundImage.loadImage(movie.image)
        binding.movieTitle.text = movie.name
        binding.ratingText.text = movie.rating.toString()
        binding.releaseYear.text = "Year :${movie.release_year.toYear()}"
        binding.movieLanguage.text = "Language: ${movie.language}"
        binding.movieDescription.text = movie.description+"\nCountry: "+movie.country+"\nCategory: "+movie.categoryProperty


    }
}