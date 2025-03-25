package com.ip_tv.ipsat.presentation.screens

import Resource
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.db.williamchart.data.Scale
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.data.local.entity.MovieBookmark
import com.ip_tv.ipsat.data.remote.CastResponse
import com.ip_tv.ipsat.databinding.DetailSeriesScreenBinding
import com.ip_tv.ipsat.domain.model.Item0
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.domain.model.SeriesDetailResponse
import com.ip_tv.ipsat.presentation.activities.PlayerSeriesActivity
import com.ip_tv.ipsat.presentation.adapters.CastDetailAdapter
import com.ip_tv.ipsat.presentation.adapters.EpisodeAdapter
import com.ip_tv.ipsat.presentation.adapters.ScreenshotsAdapter
import com.ip_tv.ipsat.presentation.viewmodel.DetailViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.ImageUtil
import com.ip_tv.ipsat.utils.LocalData
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.showSystemBars
import com.ip_tv.ipsat.utils.visible
import com.kongzue.dialogx.dialogs.WaitDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.random.Random


@AndroidEntryPoint
class DetailSeriesScreen :
    BaseFragment<DetailSeriesScreenBinding>(DetailSeriesScreenBinding::inflate) {

    private val model by viewModels<DetailViewModel>()
    private lateinit var episodeAdapter: EpisodeAdapter
    private var isBookmark = false
    override fun onViewCreate(savedInstanceState: Bundle?) {
        showSystemBars()
        val series = requireArguments().getSerializable("movie") as Movie
        LocalData.detailSeriesImage = series.image
        model.loadDetail(series.id)
        model.loadTrailer(series.name)
        model.checkBookmark(
            series
                .id
        )
        setupAdapters()
        observeModelData(series)
        observeTrailerData()
        manageBookmark()
        loadPhotos()
    }

    private fun manageBookmark() {
        model.isBookmarkResponse.observe(viewLifecycleOwner) {
            isBookmark = it
            if (it) {
                binding.bookmarkAdd.text = "Bookmarked"
                binding.bookmarkAdd.setIconResource(R.drawable.ic_favorite_black_24)
            } else {
                binding.bookmarkAdd.text = "Add Bookmark"
                binding.bookmarkAdd.setIconResource(R.drawable.ic_baseline_favorite_border_24)
            }
        }
        val movie = requireArguments().getSerializable("movie") as Movie
        binding.bookmarkAdd.setOnClickListener {
            if (isBookmark) {
                lifecycleScope.launch {
                    isBookmark = false
                    val currentBookmark = model.getCurrentBookmarkById(movie.id)
                    model.removeBookmark(
                        currentBookmark
                    )
                    binding.bookmarkAdd.text = "Add Bookmark"
                    binding.bookmarkAdd.setIconResource(R.drawable.ic_baseline_favorite_border_24)
                }
            } else {
                lifecycleScope.launch {
                    isBookmark = true
                    model.addBookmark(
                        MovieBookmark(
                            movie.id,
                            movie.name,
                            movie.image,
                            movie.categoryProperty,
                            movie.categoryid,
                            movie.country,
                            movie.description,
                            movie.language,
                            movie.rating,
                            movie.release_year,
                            true
                        )
                    )
                    binding.bookmarkAdd.text = "Bookmarked"
                    binding.bookmarkAdd.setIconResource(R.drawable.ic_favorite_black_24)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadRatingLayout() {

        // Fake random data yaratish
        val data = listOf(
            "Watching" to Random.nextInt(20, 100).toFloat(),
            "Completed" to Random.nextInt(20, 100).toFloat(),
            "Plan to Watch" to Random.nextInt(20, 100).toFloat(),
            "On Hold" to Random.nextInt(20, 100).toFloat(),
            "Dropped" to Random.nextInt(20, 100).toFloat()
        )
        with(binding.ratingView) {
            val movie = requireArguments().getSerializable("movie") as Movie
            tvRating.text = (movie.rating).toString()
            llRating.visibility = View.VISIBLE
            horizontalBar.scale = Scale(0F, 125F)
            horizontalBar.animate(data)
            tvNumScoringUsers.text = "${Random.nextInt(100)} K users"
        }
    }


    private fun loadPhotos() {
        model.photosResponse.observe(viewLifecycleOwner) { photos ->
            // screenshots
            if (photos.photos.isNotEmpty()) {
                val screenshotsAdapter =
                    ScreenshotsAdapter(requireContext())
                with(screenshotsAdapter) {
                    submitList(photos.photos)
                    onScreenshotClick = { view, item, position ->
                        ImageUtil.showFullScreenImage(
                            binding.root.context,
                            photos.photos.map {
                                it.imageUrl
                            } as ArrayList<String>,
                            view,
                            position = position
                        )
                    }
                }
                with(binding.screenshotsView) {
                    rrScreenshots.adapter = screenshotsAdapter
                    llScreenshots.visibility = View.VISIBLE
                }
            } else {
                binding.screenshotsView.root.visibility =
                    View.GONE
            }
        }
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
                    loadRatingLayout()
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

    @SuppressLint("SetTextI18n")
    fun manageUI(data: SeriesDetailResponse, movie: Movie) {
        if (data.description != null) {
            binding.textDescription.text =
                data.description + movie.description + "\n" + movie.country + "\n" + movie.categoryProperty
        }
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
        binding.castRv.visible()
        binding.tvDescriptionTitle.visible()
        val adapter = CastDetailAdapter()
        binding.castRv.adapter = adapter
        adapter.submitList(castItem.cast)
    }

}
