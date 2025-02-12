package com.ip_tv.ipsat.presentation.screens

import Resource
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.animestudios.animeapp.others.ProgressAdapter
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ItemMoviePageBinding
import com.ip_tv.ipsat.databinding.MovieVodScreenBinding
import com.ip_tv.ipsat.presentation.adapters.MovieAdapter
import com.ip_tv.ipsat.presentation.adapters.MovieCompatAdapter
import com.ip_tv.ipsat.presentation.adapters.MovieVodPageAdapter
import com.ip_tv.ipsat.presentation.viewmodel.MovieViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.showSnack
import com.kongzue.dialogx.dialogs.WaitDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieVodScreen : BaseFragment<MovieVodScreenBinding>(MovieVodScreenBinding::inflate) {
    private val model by activityViewModels<MovieViewModel>()
    private var loading = false
    private var isBannerLoaded = false
    private lateinit var animePageAdapter: MovieVodPageAdapter
    private lateinit var movieCompatAdapter: MovieCompatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isBannerLoaded) {
            model.loadBanner()
            model.loadMovies()
            model.loadSeries()
            model.loadDocumentary()
            model.loadRandomData()
            model.loadKids()
        }
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        observeModel()
        observeModelSeries()
        observeModelMovies()
        observeModelDocumentary()
        observeModelRandomData()
        observeModelRandomNextPage()
        observeModelKids()
        requireActivity().window.statusBarColor = Color.parseColor("#25B8B8B8")
        val window = requireActivity().window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val animePageBinding = ItemMoviePageBinding.inflate(
            LayoutInflater.from(requireActivity()),
            binding.root,
            false
        )
        animePageAdapter = MovieVodPageAdapter(this, animePageBinding)
        movieCompatAdapter = MovieCompatAdapter(arrayListOf())

        movieCompatAdapter.setOnItemClickListener {
            WaitDialog.setMessage("Loading..").show(requireActivity())
            lifecycleScope.launch {
                delay(300)
                model.checkMovieSeries(it.id, it).apply {
                    WaitDialog.dismiss()
                    if (this) {
                        val bundle = Bundle()
                        bundle.putSerializable("movie", it)
                        findNavController().navigate(
                            R.id.detailSeriesScreen,
                            bundle,
                            animationTransaction().build()
                        )
                    } else {
                        val bundle = Bundle()
                        bundle.putSerializable("movie", it)
                        findNavController().navigate(R.id.detailScreen, bundle)
                    }
                }
            }
        }
        val progressAdaptor = ProgressAdapter(searched = false)
        binding.animePageRecyclerView.adapter =
            ConcatAdapter(animePageAdapter, movieCompatAdapter, progressAdaptor)
        val layout = LinearLayoutManager(requireContext())
        binding.animePageRecyclerView.layoutManager = layout

        manageClicks()

        loadRefresh()

        var visible = false
        fun animate() {
            val start = if (visible) 0f else 1f
            val end = if (!visible) 0f else 1f
            ObjectAnimator.ofFloat(binding.animePageScrollTop, "scaleX", start, end).apply {
                duration = 300
                interpolator = OvershootInterpolator(2f)
                start()
            }
            ObjectAnimator.ofFloat(binding.animePageScrollTop, "scaleY", start, end).apply {
                duration = 300
                interpolator = OvershootInterpolator(2f)
                start()
            }
        }

        binding.animePageScrollTop.setOnClickListener {
            binding.animePageRecyclerView.scrollToPosition(4)
            binding.animePageRecyclerView.smoothScrollToPosition(0)
        }


        binding.animePageRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                if (!v.canScrollVertically(1)) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        model.loadNextRandomPage()
                    }
                }
                if (layout.findFirstVisibleItemPosition() > 1 && !visible) {
                    binding.animePageScrollTop.visibility = View.VISIBLE
                    visible = true
                    animate()
                }

                if (!v.canScrollVertically(-1)) {
                    visible = false
                    animate()
                    lifecycleScope.launch {
                        delay(300)
                        binding.animePageScrollTop.visibility = View.GONE
                    }
                }

                super.onScrolled(v, dx, dy)
            }
        })


    }

    private fun observeModelKids() {
        lifecycleScope.launch {
            model.kids.observe(this@MovieVodScreen) {
                when (it) {
                    is Resource.Error -> {
                        showSnack(binding.root, it.throwable.message.toString() + " KIDS")
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val movieAdapter = MovieAdapter(this@MovieVodScreen)
                        movieAdapter.setItemClickListener {
                            WaitDialog.setMessage("Loading..").show(requireActivity())
                            lifecycleScope.launch {
                                delay(300)
                                model.checkMovieSeries(it.id, it).apply {
                                    WaitDialog.dismiss()
                                    if (this) {
                                        val bundle = Bundle()
                                        bundle.putSerializable("movie", it)
                                        findNavController().navigate(
                                            R.id.detailSeriesScreen,
                                            bundle,
                                            animationTransaction().build()
                                        )
                                    } else {
                                        val bundle = Bundle()
                                        bundle.putSerializable("movie", it)
                                        findNavController().navigate(R.id.detailScreen, bundle)
                                    }
                                }
                            }
                        }
                        movieAdapter.submitList(it.data)
                        animePageAdapter.updateKids(movieAdapter)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun manageClicks() {
        animePageAdapter.setMoviesShowMoreClick {
            findNavController().navigate(
                R.id.showMoreMoviesScreen,
                null,
                animationTransaction().build()
            )
        }

        animePageAdapter.setDocumentaryShowMoreClick {
            findNavController().navigate(
                R.id.showMoreDocumentaryScreen,
                null,
                animationTransaction().build()
            )
        }

        animePageAdapter.setSeriesShowMoreClick {
            findNavController().navigate(
                R.id.showMoreSeriesScreen,
                null,
                animationTransaction().build()
            )
        }

        animePageAdapter.setKidsShowMoreClick {
            findNavController().navigate(
                R.id.showMoreKidsScreen,
                null,
                animationTransaction().build()
            )
        }

        animePageAdapter.setSearchIconClick {
            findNavController().navigate(R.id.searchScreen, null, animationTransaction().build())
        }
    }

    private fun observeModelRandomData() {
        lifecycleScope.launch {
            model.randomMovies.observe(this@MovieVodScreen) {
                when (it) {
                    is Resource.Error -> {
                        showSnack(binding.root, it.throwable.message.toString())
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        animePageAdapter.updatePopularVertical()
                        movieCompatAdapter.submitNewList(it.data)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun observeModelRandomNextPage() {
        lifecycleScope.launch {
            model.nextRandomMovies.observe(this@MovieVodScreen) {
                when (it) {
                    is Resource.Success -> {
                        movieCompatAdapter.submitList(it.data)
                    }

                    else -> {}
                }
            }
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
                        showSnack(binding.root, it.throwable.message.toString() + " MOVIES")
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val movieAdapter = MovieAdapter(this@MovieVodScreen)
                        movieAdapter.submitList(it.data)
                        movieAdapter.setItemClickListener {
                            WaitDialog.setMessage("Loading..").show(requireActivity())
                            lifecycleScope.launch {
                                delay(300)
                                model.checkMovieSeries(it.id, it).apply {
                                    WaitDialog.dismiss()
                                    if (this) {
                                        val bundle = Bundle()
                                        bundle.putSerializable("movie", it)
                                        findNavController().navigate(
                                            R.id.detailSeriesScreen,
                                            bundle,
                                            animationTransaction().build()
                                        )
                                    } else {
                                        val bundle = Bundle()
                                        bundle.putSerializable("movie", it)
                                        findNavController().navigate(R.id.detailScreen, bundle)
                                    }
                                }
                            }

                        }
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
                        showSnack(binding.root, it.throwable.message.toString() + " SERIES")
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val movieAdapter = MovieAdapter(this@MovieVodScreen)

                        movieAdapter.setItemClickListener {
                            WaitDialog.setMessage("Loading..").show(requireActivity())
                            lifecycleScope.launch {
                                delay(300)
                                model.checkMovieSeries(it.id, it).apply {
                                    WaitDialog.dismiss()
                                    if (this) {
                                        val bundle = Bundle()
                                        bundle.putSerializable("movie", it)
                                        findNavController().navigate(
                                            R.id.detailSeriesScreen,
                                            bundle,
                                            animationTransaction().build()
                                        )
                                    } else {
                                        val bundle = Bundle()
                                        bundle.putSerializable("movie", it)
                                        findNavController().navigate(R.id.detailScreen, bundle)
                                    }
                                }
                            }

                        }
                        movieAdapter.submitList(it.data)
                        animePageAdapter.updateSeries(movieAdapter)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun observeModelDocumentary() {
        lifecycleScope.launch {
            model.documentary.observe(this@MovieVodScreen) {
                when (it) {
                    is Resource.Error -> {
                        showSnack(binding.root, it.throwable.message.toString() + " DOCUMENTARY")
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val movieAdapter = MovieAdapter(this@MovieVodScreen)
                        movieAdapter.setItemClickListener {
                            WaitDialog.setMessage("Loading..").show(requireActivity())
                            lifecycleScope.launch {
                                delay(300)
                                model.checkMovieSeries(it.id, it).apply {
                                    WaitDialog.dismiss()
                                    if (this) {
                                        val bundle = Bundle()
                                        bundle.putSerializable("movie", it)
                                        findNavController().navigate(
                                            R.id.detailSeriesScreen,
                                            bundle,
                                            animationTransaction().build()
                                        )
                                    } else {
                                        val bundle = Bundle()
                                        bundle.putSerializable("movie", it)
                                        findNavController().navigate(R.id.detailScreen, bundle)
                                    }
                                }
                            }
                        }
                        movieAdapter.submitList(it.data)
                        animePageAdapter.updateDocumentary(movieAdapter)
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
            model.resetData()
            model.loadBanner()
            model.loadMovies()
            model.loadSeries()
            model.loadDocumentary()
            model.loadRandomData()
            model.loadKids()
            binding.animeRefresh.isRefreshing = false
        }
    }

}
