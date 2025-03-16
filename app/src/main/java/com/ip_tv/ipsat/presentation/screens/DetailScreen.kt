package com.ip_tv.ipsat.presentation.screens

import Resource
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.data.local.entity.MovieBookmark
import com.ip_tv.ipsat.data.remote.CastResponse
import com.ip_tv.ipsat.databinding.DetailScreenBinding
import com.ip_tv.ipsat.domain.model.Movie
import com.ip_tv.ipsat.presentation.activities.PlayerActivity
import com.ip_tv.ipsat.presentation.adapters.CastDetailAdapter
import com.ip_tv.ipsat.presentation.adapters.MovieAdapter
import com.ip_tv.ipsat.presentation.viewmodel.DetailViewModel
import com.ip_tv.ipsat.utils.BaseFragment
import com.ip_tv.ipsat.utils.DialogUtils
import com.ip_tv.ipsat.utils.animationTransaction
import com.ip_tv.ipsat.utils.animationTransactionClearStack
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.hideSystemBars
import com.ip_tv.ipsat.utils.invisible
import com.ip_tv.ipsat.utils.loadImage
import com.ip_tv.ipsat.utils.makeCustomHttpClient
import com.ip_tv.ipsat.utils.makeSslForTrailer
import com.ip_tv.ipsat.utils.showSnack
import com.ip_tv.ipsat.utils.showSystemBars
import com.ip_tv.ipsat.utils.toYear
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.net.ssl.SSLContext

@AndroidEntryPoint
class DetailScreen : BaseFragment<DetailScreenBinding>(DetailScreenBinding::inflate) {

    private val model by viewModels<DetailViewModel>()
    private var trailerUrlPlayer: String? = null
    private lateinit var loadingDialog: Dialog
    private var player: ExoPlayer? = null
    private var isChecked: Boolean = false
    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {

            error.cause?.let { cause -> Log.e("Tekshirish", "Cause: ${cause.message}") }
        }

        @SuppressLint("SwitchIntDef")
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_BUFFERING -> {}
                Player.STATE_READY -> {
                    binding.imagePoster.visibility = View.INVISIBLE
                    binding.trailerPlayContainer.visible()
                }

                Player.STATE_ENDED -> {
                    player?.play()
                    player?.seekTo(0)
                }

            }

        }
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        val movie = requireArguments().getSerializable("movie") as Movie
        val query = movie.rating
        loadingDialog = DialogUtils.loadingDialog(requireContext())
        showSystemBars()
        initializePlayer()
        model.getSearchResult(query.toString(), year = movie.release_year?.toYear() ?: "2024")
        model.loadMovieVod(movie.id)
        model.loadTrailer(movie.name)
        model.checkBookmark(movie.id)
        loadData(movie)
        observeData()
        setUpDetailSecondTypeDatas()
    }

    private fun setUpDetailSecondTypeDatas() {
        model.trailerUrl.observe(viewLifecycleOwner) { hlsUrl ->
            trailerUrlPlayer = hlsUrl.second
            prepareMedia(hlsUrl.first)
            binding.trailerPlayContainer.setOnClickListener {
                val movie = requireArguments().getSerializable("movie") as Movie
                if (hlsUrl.first.isNotEmpty()) {
                    findNavController().navigate(
                        DetailScreenDirections.actionDetailScreenToTrailerPlayerScreen(
                            hlsUrl.first,
                        ), animationTransaction().build()
                    )
                }
            }
        }
        model.isBookmarkResponse.observe(viewLifecycleOwner) { checkD ->
            isChecked = checkD
            if (checkD) {
                binding.isBookmarked.setImageResource(R.drawable.ic_favorite_black_24)
            } else {
                binding.isBookmarked.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        }
        model.castResponse.observe(viewLifecycleOwner) { cast ->
            loadCastData(cast)
        }
        binding.bookmarkContainer.setOnClickListener {
            if (isChecked) {
                val movie = requireArguments().getSerializable("movie") as Movie
                lifecycleScope.launch {
                    val currentBookmark = model.getCurrentBookmarkById(movie.id)
                    model.removeBookmark(currentBookmark)
                    binding.isBookmarked.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    isChecked = false
                }
            } else {
                val movie = requireArguments().getSerializable("movie") as Movie
                lifecycleScope.launch {
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
                            movie.release_year
                        )
                    )
                    binding.isBookmarked.setImageResource(R.drawable.ic_favorite_black_24)
                    isChecked = true
                }
            }
        }
    }

    private fun initializePlayer() {
        val context = requireContext()

        val httpDataSourceFactory =
            DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        player =
            SimpleExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory)
                .build()
                .apply {
                    setAudioAttributes(
                        com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                            .setUsage(com.google.android.exoplayer2.C.USAGE_MEDIA)
                            .setContentType(com.google.android.exoplayer2.C.AUDIO_CONTENT_TYPE_MOVIE)
                            .build(), true
                    )
                    addListener(playerListener)
                    volume = 0f
                    playWhenReady = true
                }

        binding.trailerPlayer.player = player
    }

    private fun prepareMedia(hlsUrl: String) {
        val sslContext = SSLContext.getInstance("TLS")
        makeSslForTrailer(sslContext)
        val okHttpClient = makeCustomHttpClient(sslContext)
        val okHttpDataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36")

        val dataSourceFactory =
            DefaultDataSource.Factory(requireContext(), okHttpDataSourceFactory)

        val mediaItem = MediaItem.Builder().setUri(hlsUrl)
            .setMimeType(com.google.android.exoplayer2.util.MimeTypes.APPLICATION_M3U8) // HLS format
            .build()

        val mediaSource =
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.playWhenReady = true
    }

    @SuppressLint("SetTextI18n")
    private fun observeData() {
        model.searchResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.maybeYouLikeProgressBar.gone()
                    val adapter = MovieAdapter(this)
                    adapter.setItemClickListener {
                        loadingDialog.show()
                        lifecycleScope.launch(Dispatchers.IO) {
                            delay(400)
                            withContext(Dispatchers.Main) {
                                loadingDialog.dismiss()
                            }
                            val movie = it
                            val bundle = Bundle()
                            bundle.putSerializable("movie", movie)
                            val isMovie = model.checkMovieSeries(movie.id, movie)
                            withContext(Dispatchers.Main) {
                                if (isMovie) {
                                    findNavController().navigate(
                                        R.id.detailSeriesScreen,
                                        bundle,
                                        animationTransactionClearStack(R.id.detailScreen).build()
                                    )
                                } else {
                                    findNavController().navigate(
                                        R.id.detailScreen,
                                        bundle,
                                        animationTransactionClearStack(R.id.detailScreen).build()
                                    )
                                }
                            }
                        }
                    }
                    adapter.submitList(it.data)
                    binding.similarMoviesRecycler.adapter = adapter
                }

                is Resource.Error -> {
                    binding.maybeYouLikeProgressBar.gone()
                    showSnack(binding.root, it.throwable.message.toString())
                }

                is Resource.Loading -> {
                    binding.maybeYouLikeProgressBar.visible()
                }

                else -> {}
            }
        }

        model.movieDetailResponse.observe(this) { vodMovie ->
            when (vodMovie) {
                is Resource.Success -> {

                    binding.materialButton.visible()
                    vodMovie.data?.let { videos ->
                        if (videos.urlobj.isNotEmpty()) {
                            binding.materialButton.setTextColor(requireActivity().getColor(R.color.textLightColor))
                            binding.materialButton.setOnClickListener {
                                val movie =
                                    requireArguments().getSerializable("movie") as Movie

                                PlayerActivity.currentEpIndex = 0
                                PlayerActivity.epCount = 1
                                PlayerActivity.epList = arrayListOf(
                                    vodMovie.data
                                )
                                PlayerActivity.movie = movie
                                PlayerActivity.pipStatus = true
                                val intent =
                                    PlayerActivity.newIntent(
                                        requireContext(),
                                        vodMovie.data
                                    )
                                startActivity(intent)
                            }
                        } else {
                            binding.materialButton.isEnabled = false
                            binding.materialButton.text =
                                "Movie Link was not found, Contact Admin"
                            binding.materialButton.setTextColor(requireActivity().getColor(R.color.map_red))
                        }
                    }
                }

                is Resource.Error -> {
                    showSnack(binding.root, vodMovie.throwable.message.toString())
                }

                is Resource.Loading -> {
                    binding.materialButton.invisible()
                }

                else -> {}
            }
        }

    }

    private fun loadCastData(cast: CastResponse) {
        val castDetailAdapter = CastDetailAdapter()
        binding.textCast.visible()
        binding.recyclerCast.visible()
        binding.recyclerCast.adapter = castDetailAdapter
        castDetailAdapter.submitList(cast.cast)
        castDetailAdapter.setItemClickListener {

        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(movie: Movie) {
        binding.imagePoster.loadImage(movie.image)
        binding.textTitle.text = movie.name
        binding.textRating.text = "${movie.rating.toString()}/10 IMDB"
        binding.textGenre1.text = movie.release_year!!.toYear()
        binding.textGenre2.text = movie.country
        if (movie.categoryProperty != null) {
            binding.genreContainer.visible()
            binding.textGenre3.text = movie.categoryProperty
        } else {
            binding.genreContainer.gone()
            binding.textGenre3.text = "Unknown"
        }
        if (movie.description != null) {
            binding.textDescription.text =
                movie.description + movie.description + "\n" + movie.country + "\n" + movie.categoryProperty
        }
    }

}

