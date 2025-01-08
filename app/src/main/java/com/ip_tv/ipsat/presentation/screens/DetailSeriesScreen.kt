package com.ip_tv.ipsat.presentation.screens

import Resource
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.ConcatAdapter
import com.ip_tv.ipsat.data.remote.TrailerService
import com.ip_tv.ipsat.databinding.DetailSeriesScreenBinding
import com.ip_tv.ipsat.databinding.SeriesDetailItemBinding
import com.ip_tv.ipsat.domain.model.Movie
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

    @OptIn(UnstableApi::class)
    override fun onViewCreate(savedInstanceState: Bundle?) {
        val series = requireArguments().getSerializable("movie") as Movie
        LocalData.detailSeriesImage = series.image
        model.loadDetail(series.id)
        observeModelData(series)
        pageAdapter = SeriesDetailPageAdapter(this, SeriesDetailItemBinding.inflate(layoutInflater))
        episodeAdapter = EpisodeAdapter(this)
        binding.seriesDetailPageRv.adapter = ConcatAdapter(pageAdapter, episodeAdapter)
        pageAdapter.setSubTitleClick {
            WaitDialog.show(requireActivity(),"Loading..")
            val youtubeCrawler = TrailerService()
            lifecycleScope.launch (Dispatchers.IO){
                val youtubeId = youtubeCrawler.findMovie(series.name)
                Log.d("GGG", "onViewCreate: $youtubeId")
                val intent = Intent(requireActivity(), TrailerActivity::class.java)
                intent.putExtra("apiKey", LocalData.youtube_key)
                intent.putExtra("videoId", youtubeId)
                startActivity(intent)
                WaitDialog.dismiss(requireActivity())
            }
        }

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

                    lifecycleScope.launch (Dispatchers.IO){
                        val  service = TrailerService()
                        val list =service.getCast(movie.name)
                        withContext(Dispatchers.Main){
                            binding.progress.gone()
                            binding.container.visible()
                            episodeAdapter.submitList(data.seriesList.list)
                            pageAdapter.manageUI(data, movie)
                            pageAdapter.updateCast(list)
                        }
                    }
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