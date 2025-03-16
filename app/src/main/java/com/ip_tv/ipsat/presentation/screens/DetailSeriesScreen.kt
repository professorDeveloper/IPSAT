package com.ip_tv.ipsat.presentation.screens

import Resource
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.ip_tv.ipsat.data.remote.CastResponse
import com.ip_tv.ipsat.data.remote.TrailerService
import com.ip_tv.ipsat.databinding.DetailSeriesScreenBinding
import com.ip_tv.ipsat.databinding.SeriesDetailItemBinding
import com.ip_tv.ipsat.domain.model.Cast
import com.ip_tv.ipsat.domain.model.Item0
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.presentation.activities.PlayerSeriesActivity
import com.ip_tv.ipsat.presentation.activities.TrailerActivity
import com.ip_tv.ipsat.presentation.adapters.CastAdapter
import com.ip_tv.ipsat.presentation.adapters.CastDetailAdapter
import com.ip_tv.ipsat.presentation.adapters.EpisodeAdapter
import com.ip_tv.ipsat.presentation.adapters.SeriesDetailPageAdapter
import com.ip_tv.ipsat.presentation.viewmodel.DetailViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.showSystemBars
import com.ip_tv.ipsat.utils.visible
import com.kongzue.dialogx.dialogs.WaitDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.net.ssl.SSLContext


@AndroidEntryPoint
class DetailSeriesScreen :
    BaseFragment<DetailSeriesScreenBinding>(DetailSeriesScreenBinding::inflate) {

    private val model by viewModels<DetailViewModel>()
    private lateinit var episodeAdapter: EpisodeAdapter

    override fun onViewCreate(savedInstanceState: Bundle?) {
        showSystemBars()
        val series = requireArguments().getSerializable("movie") as Movie
        LocalData.detailSeriesImage = series.image
        model.loadDetail(series.id)
        model.loadTrailer(series.name)
        setupAdapters()
        observeModelData(series)
        observeTrailerData()

    }


    private fun setupAdapters() {
        episodeAdapter = EpisodeAdapter(this)
    }

    private fun observeTrailerData() {
        model.trailerUrl.observe(viewLifecycleOwner) { hlsUrl ->
            if (hlsUrl.first.isNotEmpty()) {
                binding.epButtonText.text = "Watch Trailer"
            } else {
                binding.epButtonText.text = "No Trailer"
            }
            binding.epCard.setOnClickListener {
                if (hlsUrl.first.isNotEmpty()) {
                    findNavController().navigate(
                        DetailSeriesScreenDirections.actionDetailSeriesScreenToTrailerPlayerScreen(
                            hlsUrl.first,
                        ), animationTransaction().build()
                    )
                }
            }
        }

    }


    private fun observeModelData(movie: Movie) {
        model.seriesDetailResponse.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    binding.container.gone()
                    binding.progress.gone()
                    showSnack(binding.root, resource.throwable.message.toString())
                }

                is Resource.Loading -> {
                    binding.progress.visible()
                    binding.container.gone()
                }

                is Resource.Success -> {
                    binding.container.visible()
                    binding.progress.gone()
                    val data = resource.data

                    updateUI(movie, data)
                }

                else -> {}
            }
        }
    }

    private fun updateUI(movie: Movie, data: SeriesDetailResponse) {

        manageUI(data, movie)
        episodeAdapter.submitList(data.seriesList.list)

        episodeAdapter.setOnItemClickListener { item, position ->
            launchPlayer(data, if (position != 0) position - 1 else 0)
        }
        binding.epCard.setOnClickListener {
        }
    }


    private fun launchPlayer(
        data: SeriesDetailResponse,
        position: Int,
    ) {
        lifecycleScope.launch {
            WaitDialog.show(requireActivity(), "Loading..")

            PlayerSeriesActivity.currentEpIndex = position
            PlayerSeriesActivity.epCount = data.seriesList.list.size
            PlayerSeriesActivity.epList = data.seriesList.list as ArrayList<Item0>
            PlayerSeriesActivity.movieInfo = data
            PlayerSeriesActivity.pipStatus = true
            Log.d("GGG", "position0 position: $position")

            WaitDialog.dismiss()
            startActivity(PlayerSeriesActivity.newIntent(requireContext()))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        LocalData.detailSeriesImage = ""
    }

    fun manageUI(data: SeriesDetailResponse, movie: Movie) {
        binding.episodeRv.adapter = episodeAdapter
        binding.tvMovieTitleValue.text = movie.name
        binding.tvVoteAverage.text = movie.rating.toString()
        binding.ivPoster.loadImage(movie.image)
        binding.yearValue.text = data.releaseYear
        binding.durationValue.text = data.language
        binding.ivBackdrop.loadImage(data.horizontalPoster)
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvGnreValue.text =
            "Property:" + data.property + "  Director: " + data.director


        binding.tvEpisodeTitle.text = "Episodes ${data.seriesList.totalNum} Count"
        model.castResponse.observe(viewLifecycleOwner) { cast ->
            updateCast(cast)

        }
    }

    fun updateCast(castItem: CastResponse) {
        val adapter = CastDetailAdapter()
        binding.castRv.adapter = adapter
        adapter.submitList(castItem.cast)
    }

}
