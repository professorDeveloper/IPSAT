package com.ip_tv.ipsat.presentation.screens

import Resource
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.ip_tv.ipsat.databinding.DetailSeriesScreenBinding
import com.ip_tv.ipsat.databinding.SeriesDetailItemBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.presentation.adapters.EpisodeAdapter
import com.ip_tv.ipsat.presentation.adapters.SeriesDetailPageAdapter
import com.ip_tv.ipsat.presentation.viewmodel.DetailViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSeriesScreen :
    BaseFragment<DetailSeriesScreenBinding>(DetailSeriesScreenBinding::inflate) {
    private val model by viewModels<DetailViewModel>()
    private lateinit var pageAdapter: SeriesDetailPageAdapter
    private lateinit var episodeAdapter: EpisodeAdapter

    override fun onViewCreate(savedInstanceState: Bundle?) {
        val series = requireArguments().getSerializable("movie") as Movie
        LocalData.detailSeriesImage = series.image
        model.loadDetail(series.id)
        observeModelData(series)
        pageAdapter = SeriesDetailPageAdapter(this, SeriesDetailItemBinding.inflate(layoutInflater))
        episodeAdapter = EpisodeAdapter(this)
        binding.seriesDetailPageRv.adapter =ConcatAdapter(pageAdapter,episodeAdapter)


    }

    private fun observeModelData(movie: Movie) {
        model.seriesDetailResponse.observe(this) {
            when (it) {
                is Resource.Error -> {
                    binding.container.gone()
                    binding.progress.gone()
                    showSnack(binding.root, it.throwable.message.toString())
                }

                is Resource.Loading -> {
                    binding.progress.visible()
                    binding.container.gone()
                }

                is Resource.Success -> {
                    val data = it.data
                    binding.progress.gone()
                    binding.container.visible()
                    episodeAdapter.submitList(data.seriesList.list)
                    pageAdapter.manageUI(data, movie)
                }

                else -> {

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalData.detailSeriesImage = ""
    }

}