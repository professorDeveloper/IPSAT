package com.ip_tv.ipsat.presentation.screens

import Resource
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.ip_tv.ipsat.data.remote.TrailerService
import com.ip_tv.ipsat.databinding.DetailSeriesScreenBinding
import com.ip_tv.ipsat.databinding.SeriesDetailItemBinding
import com.ip_tv.ipsat.domain.model.Item0
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.presentation.activities.PlayerSeriesActivity
import com.ip_tv.ipsat.presentation.activities.TrailerActivity
import com.ip_tv.ipsat.presentation.adapters.EpisodeAdapter
import com.ip_tv.ipsat.presentation.adapters.SeriesDetailPageAdapter
import com.ip_tv.ipsat.presentation.viewmodel.DetailViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.visible
import com.kongzue.dialogx.dialogs.WaitDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        setupAdapters()
        observeModelData(series)


        lifecycleScope.launch {
            val castList = withContext(Dispatchers.IO) {
                TrailerService().getCast(series.name)
            }
            pageAdapter.updateCast(castList)
        }

        pageAdapter.setSubTitleClick {
            loadTrailer(series.name)
        }
    }

    private fun setupAdapters() {
        pageAdapter = SeriesDetailPageAdapter(this, SeriesDetailItemBinding.inflate(layoutInflater))
        episodeAdapter = EpisodeAdapter(this)
        binding.seriesDetailPageRv.adapter = ConcatAdapter(pageAdapter, episodeAdapter)
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

        pageAdapter.manageUI(data, movie)

        episodeAdapter.submitList(data.seriesList.list)

        episodeAdapter.setOnItemClickListener { item, position ->
            launchPlayer(data, position - 1, )
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

    private fun loadTrailer(movieName: String) {
        WaitDialog.show(requireActivity(), "Loading..")
        lifecycleScope.launch(Dispatchers.IO) {
            val youtubeId = TrailerService().findMovie(movieName)
            Log.d("GGG", "YouTube ID: $youtubeId")

            withContext(Dispatchers.Main) {
                val intent = Intent(requireActivity(), TrailerActivity::class.java)
                intent.putExtra("apiKey", LocalData.youtube_key)
                intent.putExtra("videoId", youtubeId)
                startActivity(intent)
                WaitDialog.dismiss(requireActivity())
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalData.detailSeriesImage = ""
    }
}
