package com.ip_tv.ipsat.presentation.screens

import Resource
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ip_tv.ipsat.databinding.ItemMoviePageBinding
import com.ip_tv.ipsat.databinding.MovieVodScreenBinding
import com.ip_tv.ipsat.presentation.adapters.MovieAdapter
import com.ip_tv.ipsat.presentation.adapters.MovieVodPageAdapter
import com.ip_tv.ipsat.presentation.viewmodel.MovieViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.navBarHeight
import com.ip_tv.ipsat.utils.px
import com.ip_tv.ipsat.utils.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieVodScreen : BaseFragment<MovieVodScreenBinding>(MovieVodScreenBinding::inflate) {
    private val model by activityViewModels<MovieViewModel>()
    private var isBannerLoaded = false
    private lateinit var animePageAdapter: MovieVodPageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isBannerLoaded) {
            model.loadBanner()
            model.loadNextPage()
            model.loadSeries()
        }
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        observeModel()
        observeModelSeries()
        observeModelMovies()
        requireActivity().window.statusBarColor = Color.parseColor("#25B8B8B8")


        val animePageBinding = ItemMoviePageBinding.inflate(LayoutInflater.from(requireActivity()), binding.root, false)
        animePageAdapter = MovieVodPageAdapter(this,animePageBinding)

        binding.animePageRecyclerView.adapter = animePageAdapter
        val layout = LinearLayoutManager(requireContext())
        binding.animePageRecyclerView.layoutManager = layout

        loadRefresh()

        binding.animePageScrollTop.setOnClickListener {
            binding.animePageRecyclerView.scrollToPosition(4)
            binding.animePageRecyclerView.smoothScrollToPosition(0)
        }

    }

    private fun observeModel() {
        lifecycleScope.launch {
            model.initBanner
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { animePageAdapter.handleBannerState(it) }
        }
    }


    private fun observeModelMovies() {
        lifecycleScope.launch {
            model.movies.observe(this@MovieVodScreen) {
                when (it) {
                    is Resource.Error -> {
                        showSnack(binding.root, it.throwable.message.toString())
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val movieAdapter = MovieAdapter()
                        movieAdapter.submitList(it.data)
                        animePageAdapter.updateRecent(movieAdapter)
                    }

                    else -> {}
                }
            }
        }
    }
    private fun observeModelSeries() {
        lifecycleScope.launch {
            model.series.observe(this@MovieVodScreen) {
                when (it) {
                    is Resource.Error -> {
                        showSnack(binding.root, it.throwable.message.toString())
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val movieAdapter = MovieAdapter()
                        movieAdapter.submitList(it.data)
                        animePageAdapter.updateSeries(movieAdapter)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadRefresh() {
        binding.animeRefresh.setSlingshotDistance(128)
        binding.animeRefresh.setProgressViewEndTarget(false, 128)
        binding.animeRefresh.setOnRefreshListener {
//            Refresh.activity[this.hashCode()]!!.postValue(true)
        }
    }

}
