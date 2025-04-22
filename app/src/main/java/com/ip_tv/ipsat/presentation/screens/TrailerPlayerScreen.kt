package com.ip_tv.ipsat.presentation.screens

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ExoPlayerControlViewBinding
import com.ip_tv.ipsat.databinding.TrailerPlayerScreenBinding
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.hideSystemBars
import com.ip_tv.ipsat.utils.initActivity
import com.ip_tv.ipsat.utils.makeCustomHttpClient
import com.ip_tv.ipsat.utils.makeSslForTrailer
import com.ip_tv.ipsat.utils.visible
import javax.net.ssl.SSLContext

class TrailerPlayerScreen : Fragment() {
    private var _binding: TrailerPlayerScreenBinding? = null
    private var player: ExoPlayer? = null
    private val binding get() = _binding!!
    private val args by navArgs<TrailerPlayerScreenArgs>()

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
                    binding.loadingLayout.visibility = View.INVISIBLE
                    binding.pvPlayer.visible()
                    binding.pvPlayer.findViewById<ImageButton>(R.id.exo_next_ep).gone()
                    binding.pvPlayer.findViewById<ImageButton>(R.id.exo_prev_ep).gone()
                    binding.pvPlayer.findViewById<LinearLayout>(R.id.exo_bottom_cont).gone()
                    binding.pvPlayer.findViewById<LinearLayout>(R.id.exo_top_cont).gone()
                    binding.pvPlayer.findViewById<ImageView>(R.id.exo_lock).visibility = View.GONE
                }

                Player.STATE_ENDED -> {
                    player?.play()
                    player?.seekTo(0)
                }

            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TrailerPlayerScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActivity(requireActivity())
        setUpPlayer()
        hideSystemBars()
        prepareMedia(hlsUrl = args.url)

    }

    private fun setUpPlayer() {
        val context = requireContext()
        val httpDataSourceFactory =
            DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        player = SimpleExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).build()
            .apply {
                setAudioAttributes(
                    com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                        .setUsage(com.google.android.exoplayer2.C.USAGE_MEDIA)
                        .setContentType(com.google.android.exoplayer2.C.CONTENT_TYPE_MOVIE)
                        .build(), true
                )
                volume = 0f
                playWhenReady = true
                addListener(playerListener)
            }

        binding.pvPlayer.player = player
    }

    private fun prepareMedia(hlsUrl: String) {
        val sslContext = SSLContext.getInstance("TLS")
        makeSslForTrailer(sslContext)
        val okHttpClient = makeCustomHttpClient(sslContext)
        val okHttpDataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36")

        val dataSourceFactory = DefaultDataSource.Factory(requireContext(), okHttpDataSourceFactory)

        val mediaItem = MediaItem.Builder().setUri(hlsUrl)
            .setMimeType(com.google.android.exoplayer2.util.MimeTypes.APPLICATION_M3U8) // HLS format
            .build()

        val mediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.playWhenReady = true
    }
}