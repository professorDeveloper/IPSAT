package com.ip_tv.ipsat.presentation.screens

import android.os.Bundle
import android.os.Parcelable
import android.speech.SpeechRecognizer
import android.view.View
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ip_tv.ipsat.domain.model.SearchResults
import com.animestudios.animeapp.others.ProgressAdapter
import com.ip_tv.ipsat.databinding.ShowMoreMoviesScreenBinding
import com.ip_tv.ipsat.presentation.adapters.ShowMoreItemAdapter
import com.ip_tv.ipsat.presentation.adapters.ShowMoreMovieAdapter
import com.ip_tv.ipsat.presentation.viewmodel.ShowMoreMovieViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.statusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
@AndroidEntryPoint
class ShowMoreMoviesScreen :
    BaseFragment<ShowMoreMoviesScreenBinding>(ShowMoreMoviesScreenBinding::inflate) {
    private val scope = lifecycleScope
    var lastSearchedText = ""
    private var screenWidth: Float = 0f

    val model: ShowMoreMovieViewModel by viewModels()

    override fun onViewCreate(savedInstanceState: Bundle?) {
        screenWidth = resources.displayMetrics.run { widthPixels / density }
        binding.apply { }
        binding.searchRecyclerView.updatePaddingRelative(
            top = statusBarHeight,
        )


        val headerAdaptor = ShowMoreMovieAdapter(this)
        initProgress(headerAdaptor)
        val gridSize = (screenWidth / 124f).toInt()
        val gridLayoutManager = GridLayoutManager(requireContext(), gridSize)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> gridSize
                    concatAdapter.itemCount - 1 -> gridSize
                    else -> when (style) {
                        0 -> 1
                        else -> gridSize
                    }
                }
            }
        }
        binding.searchRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
                if (!v.canScrollVertically(1)) {
                    if (model.searchResults.hasNextPage && model.searchResults.results.isNotEmpty() && !loading) {
                        scope.launch(Dispatchers.IO) {
                            model.loadNextPage(model.searchResults)
                        }
                    }
                }
                super.onScrolled(v, dx, dy)
            }
        })

        binding.searchRecyclerView.layoutManager = gridLayoutManager
        binding.searchRecyclerView.adapter = concatAdapter

        model.result.observe(viewLifecycleOwner) {
                model.searchResults.apply {
                    rating = it!!.rating
                    genres = it.genres
                    tags = it.tags
                    country = it.country
                    releaseYear = it.releaseYear
                    format = it.format
                    page = it.page
                }

                val prev = model.searchResults.results.size

                model.searchResults.results.addAll(it!!.results)
                mediaAdaptor.notifyItemRangeInserted(prev, it.results.size)
                progressAdapter.bar?.visibility = if (it.hasNextPage) View.VISIBLE else View.GONE
        }

    }


    private fun initProgress(headerAdaptor: ShowMoreMovieAdapter) {
        val notSet = model.notSet
        progressAdapter = ProgressAdapter(searched = model.searched)

        progressAdapter.ready.observe(viewLifecycleOwner) {
            if (it == true) {
                if (!notSet) {
                    if (!model.searched) {
                        model.searched = true
                        headerAdaptor.search?.run()
                    }
                } else
                    headerAdaptor.requestFocus?.run()
                if (requireActivity().intent.getBooleanExtra("search", false)) search()

            }
        }

    }

    private var searchTimer = Timer()
    private var loading = false
    fun search() {
        val size = model.searchResults.results.size
        model.searchResults.results.clear()
        requireActivity().runOnUiThread {
            mediaAdaptor.notifyItemRangeRemoved(0, size)
        }

        progressAdapter.bar?.visibility = View.VISIBLE

        searchTimer.cancel()
        searchTimer.purge()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                scope.launch(Dispatchers.IO) {
                    loading = true
                    model.loadSearch(result)

                    loading = false
                }
            }
        }
        searchTimer = Timer()
        searchTimer.schedule(timerTask, 500)
    }

    var state: Parcelable? = null

}
