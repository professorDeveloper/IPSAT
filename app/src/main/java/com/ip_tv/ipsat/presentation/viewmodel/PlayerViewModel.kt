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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val app: Application,
    val player: ExoPlayer,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var mediaSession: MediaSessionCompat =
        MediaSessionCompat(app, "AnimeScrap Media Session")
    private val _animeStreamLink: MutableLiveData<String> = MutableLiveData()
    private val animeStreamLink: LiveData<String> = _animeStreamLink
    var downloadLink = MutableLiveData<String>()
    private val isAutoPlayEnabled = true
    private val isVideoCacheEnabled = true

    val isLoading = MutableLiveData(true)
    val keepScreenOn = MutableLiveData(false)
    val showSubsBtn = MutableLiveData(true)
    val playNextEp = MutableLiveData(false)
    val isError = MutableLiveData(false)

    private var qualityMapUnsorted: MutableMap<String, Int> = mutableMapOf()
    var qualityMapSorted: MutableMap<String, Int> = mutableMapOf()
    var qualityTrackGroup: com.google.android.exoplayer2.Tracks.Group? = null

    private var mediaSessionConnector: MediaSessionConnector = MediaSessionConnector(mediaSession)

    private var simpleCache: SimpleCache? = null
    private val databaseProvider =
        com.google.android.exoplayer2.database.StandaloneDatabaseProvider(app)

    private val savedDone = savedStateHandle.getStateFlow("done", false)

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
                Log.d("GGG", "onLoadError: ${error.message}")
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
    fun updateQuality(newUrl: String) {
        val currentPosition = player.currentPosition
        val isPlaying = player.isPlaying
        isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _animeStreamLink.postValue(newUrl)

                withContext(Dispatchers.Main) {
                    val mediaItem: MediaItem = MediaItem.fromUri(newUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.seekTo(currentPosition)
                    if (isPlaying) {
                        player.play()
                    }
                    isLoading.postValue(false)
                }
            }
        }
    }

    private fun getCustomPlayerListener(): Player.Listener {
        return object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == PlaybackState.STATE_NONE || playbackState == PlaybackState.STATE_CONNECTING || playbackState == PlaybackState.STATE_STOPPED)
                    isLoading.postValue(true)
                else
                    isLoading.postValue(false)
                super.onPlaybackStateChanged(playbackState)
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS, PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED
                    -> {
                        snackString("Source Exception : ${error.message}")
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
                if (progress <= 0 && isAutoPlayEnabled && !isPlaying)
                    playNextEp.postValue(true)
            }

            override fun onTracksChanged(tracks: com.google.android.exoplayer2.Tracks) {
                for (trackGroup in tracks.groups) {
                    if (trackGroup.type == C.TRACK_TYPE_VIDEO) {
                        for (i in 0 until trackGroup.length) {
                            val trackFormat = trackGroup.getTrackFormat(i).height
                            if (trackGroup.isTrackSupported(i) && trackGroup.isTrackSelected(i)) {
                                qualityMapUnsorted["${trackFormat}p"] = i
                            }
                        }
                        qualityMapUnsorted.entries.sortedBy { it.key.replace("p", "").toInt() }
                            .reversed().forEach { qualityMapSorted[it.key] = it.value }

                        qualityTrackGroup = trackGroup
                    }

                }
            }
        }

    }

    fun setAnimeLink(
        animeUrl: String,
        getNextEp: Boolean = false
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                println(
                    "STREAM GET LINK"
                )
                println(animeUrl)
                animeUrl.apply {
                    _animeStreamLink.postValue(this@apply)
                    withContext(Dispatchers.Main) {
                        if (!savedDone.value || getNextEp) {
                            println("prepare Media Source")
                            prepareMediaSource()
                            downloadLink.value = this@apply
                            savedStateHandle["done"] = true
                        }
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

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
        releaseCache()
    }

    private fun releasePlayer() {
        player.release()
        mediaSession.release()
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

}