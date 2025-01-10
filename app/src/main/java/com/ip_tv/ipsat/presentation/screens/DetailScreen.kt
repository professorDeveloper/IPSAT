package com.ip_tv.ipsat.presentation.screens

import Resource
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.DetailScreenBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.presentation.adapters.MovieAdapter
import com.ip_tv.ipsat.presentation.viewmodel.DetailViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.animationTransactionClearStack
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.invisible
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.toYear
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DetailScreen : BaseFragment<DetailScreenBinding>(DetailScreenBinding::inflate) {

    private val model by viewModels<DetailViewModel>()

    override fun onViewCreate(savedInstanceState: Bundle?) {
        val movie = requireArguments().getSerializable("movie") as Movie
        val query = movie.rating
        model.getSearchResult(query.toString(), year = movie.release_year?.toYear()?:"2024")
        model.loadMovieVod(movie.id)
        loadData(movie)
        observeData()
    }

    @SuppressLint("SetTextI18n")
    private fun observeData() {
        model.searchResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progresMayYouLike.gone()

                    val adapter = MovieAdapter(this)
                    adapter.setItemClickListener {
                        binding.container.gone()
                        binding.rootProgress.visible()
                        lifecycleScope.launch (Dispatchers.IO){
                            delay(400)
                            withContext(Dispatchers.Main) {
                                binding.rootProgress.gone()
                            }
                            val movie = it
                            val bundle = Bundle()
                            bundle.putSerializable("movie", movie)
                            val isMovie = model.checkMovieSeries(movie.id, movie)
                            withContext(Dispatchers.Main) {
                                if (isMovie) {
                                    findNavController().navigate(R.id.detailSeriesScreen, bundle, animationTransactionClearStack(R.id.detailScreen).build())
                                } else {
                                    findNavController().navigate(R.id.detailScreen, bundle, animationTransactionClearStack(R.id.detailScreen).build())
                                }
                            }
                        }
                    }
                    adapter.submitList(it.data)
                    binding.similarMoviesRecycler.adapter = adapter
                }

                is Resource.Error -> {
                    binding.progresMayYouLike.gone()
                    showSnack(binding.root, it.throwable.message.toString())
                }

                is Resource.Loading -> {
                    binding.progresMayYouLike.visible()
                }

                else -> {}
            }
        }

        model.movieDetailResponse.observe(this) {
            when(it) {
                is Resource.Success -> {
                    binding.qualityProgress.gone()
                    binding.materialButton.visible()
                    it.data?.let { videos ->
                        if (videos.urlobj.isNotEmpty()) {
                            binding.materialButton.setTextColor(requireActivity().getColor(R.color.textLightColor))
                            binding.materialButton.setOnClickListener {
                                val movie = requireArguments().getSerializable("movie") as Movie
                                if (videos.urlobj.isNotEmpty()){

                                }
                            }
                        }else {
                            binding.materialButton.isEnabled=false
                            binding.materialButton.text="Movie Link was not found, Contact Admin"
                            binding.materialButton.setTextColor(requireActivity().getColor(R.color.map_red))
                        }
                    }
                }
                is Resource.Error -> {
                    showSnack(binding.root, it.throwable.message.toString())
                }
                is Resource.Loading -> {

                    binding.qualityProgress.visible()
                    binding.materialButton.invisible()
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
        binding.releaseYear.text = "Year :${movie.release_year!!.toYear()}"
        binding.movieLanguage.text = "Language: ${movie.language}"
        makeSpannable(movie = movie)

    }

    private fun makeSpannable(movie: Movie) {
        val description = "Description: "
        val country = "Country: "
        val category = "Category: "

        val descriptionValue = movie.description.toString()
        val countryValue = movie.country
        val categoryValue = movie.categoryProperty

        val primaryColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        val spannable = SpannableString(
            "$description$descriptionValue\n$country$countryValue\n$category$categoryValue"
        )

        spannable.setSpan(
            ForegroundColorSpan(primaryColor),
            0,
            description.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(primaryColor),
            description.toString().length + descriptionValue.length + 1,
            description.length + descriptionValue.length + 1 + country.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(primaryColor),
            spannable.length - (category.length + categoryValue.toString().length),
            spannable.length - categoryValue.toString().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.movieDescription.text = spannable
    }
}

