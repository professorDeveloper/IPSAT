package com.ip_tv.ipsat.presentation.viewmodel

import android.app.Application
import android.media.session.PlaybackState
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroup
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ip_tv.ipsat.domain.model.VodMovieResponse
import com.ip_tv.ipsat.domain.repository.DetailRepository
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val app: Application,
    val player: ExoPlayer,
    private val repository: DetailRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var mediaSession: MediaSessionCompat =
        MediaSessionCompat(app, "AnimeScrap Media Session")
    private val _animeStreamLink: MutableLiveData<String> = MutableLiveData()
    private val animeStreamLink: LiveData<String> = _animeStreamLink
    private val vodData: MutableLiveData<VodMovieResponse> = MutableLiveData()
    private val isAutoPlayEnabled = true
    private val isVideoCacheEnabled = true

    val isLoading = MutableLiveData(true)
    val keepScreenOn = MutableLiveData(false)
    val showSubsBtn = MutableLiveData(true)
    val playNextEp = MutableLiveData(false)
    val isError = MutableLiveData(false)

    private var qualityMapUnsorted: MutableMap<String, Int> = mutableMapOf()
    var qualityMapSorted: MutableMap<String, Int> = mutableMapOf()
    var qualityTrackGroup: TrackGroup? = null

    private var mediaSessionConnector: MediaSessionConnector = MediaSessionConnector(mediaSession)

    private var simpleCache: SimpleCache? = null
    private val databaseProvider =
        com.google.android.exoplayer2.database.StandaloneDatabaseProvider(app)

    private val savedDone = savedStateHandle.getStateFlow("done", false)
    var isSeriesMode = false

    fun loadVod(id: Int) {
        viewModelScope.launch {
            repository.getSeriesVod(id = id).onEach {
                it.onSuccess {
                    vodData.postValue(it)
                }
                it.onFailure {

                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateQuality(newUrl: String) {
        // Save the current playback position before changing quality
        val currentPosition = player.currentPosition
        val isPlaying = player.isPlaying

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _animeStreamLink.postValue(newUrl)

                withContext(Dispatchers.Main) {
                    val mediaItem: MediaItem = MediaItem.fromUri(newUrl)
                    player.setMediaItem(mediaItem)

                    // Seek to the saved position
                    player.prepare()
                    player.seekTo(currentPosition)

                    // Resume playback if it was playing
                    if (isPlaying) {
                        player.play()
                    }

                    isLoading.postValue(false)
                }
            }
        }
    }

    init {
        player.prepare()
        player.playWhenReady = true
        mediaSessionConnector.setPlayer(player)
        mediaSession.isActive = true
        player.addListener(getCustomPlayerListener())
        player.addAnalyticsListener(object : AnalyticsListener {
            override fun onLoadError(
                eventTime: AnalyticsListener.EventTime,
                loadEventInfo: LoadEventInfo,
                mediaLoadData: MediaLoadData,
                error: IOException,
                wasCanceled: Boolean
            ) {
                Log.d("GGG", error.message.toString())
                Log.d("GGG", "onLoadError: ${error.cause}")
            }
        })


        // Cache
        simpleCache?.release()
        simpleCache = com.google.android.exoplayer2.upstream.cache.SimpleCache(
            File(
                app.cacheDir,
                "exoplayerSourceCache"
            ).also { it.deleteOnExit() }, // Ensures always fresh file
            com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor(300L * 1024L * 1024L),
            databaseProvider
        )
    }

    private fun getCustomPlayerListener(): Player.Listener {
        return object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == PlaybackState.STATE_NONE || playbackState == PlaybackState.STATE_CONNECTING || playbackState == PlaybackState.STATE_STOPPED) {
                    Log.d("GGG", "onPlaybackStateChanged:${playbackState} TRUEEEE FUCK  ")
                    isLoading.postValue(true)
                } else {
                    Log.d("GGG", "onPlaybackStateChanged:${playbackState} FALSEEE FUCK  ")
                    isLoading.postValue(false)
                }
                super.onPlaybackStateChanged(playbackState)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS, PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED
                    -> {
                        snackString("Source Exception : ${error.message}")
                    }

                    ExoPlaybackException.ERROR_CODE_DECODING_FAILED -> {
                        player.stop()
                        player.prepare()
                        player.play()
                        isLoading.postValue(false)
                    }

                    else
                    -> toast("Player Error ${error.errorCode} (${error.errorCodeName}) : ${error.message}")
                }
                Toast.makeText(app, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                keepScreenOn.postValue(isPlaying)
                val progress = player.duration - player.currentPosition
                if (progress <= 0 && isAutoPlayEnabled && !isPlaying) {
                    isLoading.postValue(false)
                    playNextEp.postValue(true)
                }
            }
//
//            override fun onTracksChanged(tracks: com.google.android.exoplayer2.Tracks) {
//                // Update UI using current tracks.
//                for (trackGroup in tracks.groups) {
//                    // Group level information.
//                    if (trackGroup.type == C.TRACK_TYPE_VIDEO) {
//                        for (i in 0 until trackGroup.length) {
//                            val trackFormat = trackGroup.getTrackFormat(i).height
//                            if (trackGroup.isTrackSupported(i) && trackGroup.isTrackSelected(i)) {
//                                qualityMapUnsorted["${trackFormat}p"] = i
//                            }
//                        }
//                        qualityMapUnsorted.entries.sortedBy { it.key.replace("p", "").toInt() }
//                            .reversed().forEach { qualityMapSorted[it.key] = it.value }
//
//                        qualityTrackGroup = trackGroup
//                    }
//
//                }
//            }
//        }
        }

    }


    fun setAnimeLink(animeUrl: String, getNextEp: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _animeStreamLink.postValue(animeUrl)
                withContext(Dispatchers.Main) {
                    if (!savedDone.value || getNextEp) {
                        prepareMediaSource()
                        savedStateHandle["done"] = true
                    }

                }
            }
        }
    }


    private fun releaseCache() {
        simpleCache?.release()
        simpleCache = null
    }

    private fun setMediaSource(mediaSource: MediaSource) {
        println("Set media Source")
        player.stop()
        player.prepare()
        qualityMapSorted = mutableMapOf()
        qualityMapUnsorted = mutableMapOf()
        qualityTrackGroup = null
        showSubsBtn.postValue(false)
        player.setMediaSource(mediaSource)
    }


    private fun prepareMediaSource() {
        if (animeStreamLink.value == null) return
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setReadTimeoutMs(20000)
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(20000)
        val mediaItem: MediaItem = MediaItem.fromUri(animeStreamLink.value!!.toUri())
        val mediaSource: MediaSource = if (isVideoCacheEnabled) {

            val cacheFactory = CacheDataSource.Factory().apply {
                setCache(simpleCache!!)
                setUpstreamDataSourceFactory(dataSourceFactory)
            }
            DefaultMediaSourceFactory(cacheFactory)
                .createMediaSource(mediaItem)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
        }


        setMediaSource(mediaSource)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
        releaseCache()
    }

    private fun releasePlayer() {
        player.release()
        mediaSession.release()
    }


}