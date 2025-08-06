package com.ip_tv.ipsat.presentation.activities

import Resource
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.Dialog
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.bugsnag.android.Bugsnag
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.LocalMediaDrmCallback
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.snackbar.Snackbar
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.app.App
import com.ip_tv.ipsat.data.local.mapper.toChannelsResponse
import com.ip_tv.ipsat.data.local.mapper.toEventModelItem
import com.ip_tv.ipsat.databinding.ActivityLiveTvBinding
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
import com.ip_tv.ipsat.domain.model.ChannelLinkResponse
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.domain.model.EventModel
import com.ip_tv.ipsat.domain.model.EventModelItem
import com.ip_tv.ipsat.presentation.adapters.CustomAdapter
import com.ip_tv.ipsat.presentation.adapters.PlayerCategoryAdapter
import com.ip_tv.ipsat.presentation.adapters.PlayerChannelAdapter
import com.ip_tv.ipsat.presentation.viewmodel.LiveTvScreenViewModel
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.hideSystemBars
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import kotlin.math.min

@AndroidEntryPoint
class LiveTvActivity : AppCompatActivity(), Player.Listener {

    private lateinit var binding: ActivityLiveTvBinding
    private lateinit var player: ExoPlayer
    private val viewModel by viewModels<LiveTvScreenViewModel>()

    private lateinit var exoPlay: ImageView
    private lateinit var exoQuality: ImageButton
    private lateinit var exoRotate: ImageButton
    private lateinit var exoPip: ImageButton
    private lateinit var scaleBtn: ImageButton

    private lateinit var exoToggleButton: FrameLayout
    private lateinit var exoToggleButtonRight: FrameLayout
    private lateinit var exoTitle: TextView
    private lateinit var exoProgress: PlayerActivity.ExtendedTimeBar

    private var notchHeight: Int = 0
    private var isNewChannelSelected = false
    private var isFullscreen: Int = 0
    private lateinit var categoryAdapter: PlayerCategoryAdapter
    private lateinit var channelAdapter: PlayerChannelAdapter

    private lateinit var selectedChannel: ChannelResponseItem

    private lateinit var videoName: TextView
    private lateinit var videoInfo: TextView
    private lateinit var serverInfo: TextView
    private var isNormal = true
    private lateinit var trackSelector: DefaultTrackSelector

    var rotation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveTvBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        setupFullScreenMode()
        onBackPressedDispatcher.addCallback(this) {
            finishAndRemoveTask()
        }
        parseIntentData()
        trackSelector = DefaultTrackSelector(this)
        setupPlayer()
        setupUI()
        val hasChannels =
            eventList.find { it.category == currentCategory?.id && it.id == selectedChannel.id }
        Log.d("GGG", "onCreate:${hasChannels == null} ")
        if (hasChannels == null) {
            viewModel.loadChannelsByCategory(currentCategory!!.id)
            viewModel.loadChannelUrl(selectedChannel.id.toInt().toString())
        } else {
            Log.d("GGG", "onCreate:Tuwdii ")
            viewModel.loadEventChannels()
            loadEventChannel(selectedChannel.toEventModelItem())
        }

        setupRecyclerViews()
        observeViewModel()
    }

    @SuppressLint("StringFormatInvalid")
    private fun changeVideoSpeed(byInt: Float) {
        player.playbackParameters = PlaybackParameters(byInt)
    }


    private fun setupFullScreenMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemBars()
        WindowInsetsControllerCompat(window, binding.root).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun parseIntentData() {
        selectedChannel = intent.getSerializableExtra(EXTRA_CHANNEL_DATA) as ChannelResponseItem
    }

    private fun getVideoQuality(): String {
        val trackGroups = player.currentTrackGroups

        for (i in 0 until trackGroups.length) {
            val format = trackGroups.get(i).getFormat(0)

            if (format.height > 0 && format.width > 0) {
                val quality = "${format.width}x${format.height} @ ${format.bitrate / 1000} kbps"
                return quality
                break
            }
        }
        return ""
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build().apply {
            binding.player.player = this
            playWhenReady = true
        }
        viewModel.animeLink.observe(this) {
            setUpName()
            loadChannel(it)
        }
    }

    private fun setUpName() {
        val quality = getVideoQuality()
        exoTitle.text = selectedChannel.name
        serverInfo.text = selectedChannel.name
        videoInfo.text = quality
        videoName.text = quality
    }

    private fun String.hexToBase64(): String {
        val bytes = ByteArray(length / 2) { index ->
            substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun loadEventChannel(data: EventModelItem) {
        Log.d("GGG", "loadEventChannel:${data.clearkey} ")
        if (data.clearkey != null) {
            Log.d("GGG", "loadEventChannel:${data.clearkey.toString().equals("Null")} ")
            if (!data.clearkey.toString().equals("Null")) {
                val (drmKeyId, drmKey) = data.clearkey.split(":", limit = 2).let {
                    it.takeIf { it.size == 2 } ?: error("Invalid clearkey format")
                }
                val mpdUrl = data.play_url
                val drmKeyBytes = drmKey.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val encodedDrmKey = Base64.encodeToString(
                    drmKeyBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )

                val drmKeyIdBytes =
                    drmKeyId.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val encodedDrmKeyId = Base64.encodeToString(
                    drmKeyIdBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )
                val drmBody = """
        {
          "keys": [
            {
              "kty": "oct",
              "k": "$encodedDrmKey",
              "kid": "$encodedDrmKeyId"
            }
          ],
          "type": "temporary"
        }
    """.trimIndent()

                val dashMediaItem =
                    MediaItem.Builder().setUri(mpdUrl).setMimeType(MimeTypes.APPLICATION_MPD)
                        .setMediaMetadata(
                            MediaMetadata.Builder().setTitle("ClearKey Playback").build()
                        ).build()


                val drmCallback = LocalMediaDrmCallback(drmBody.toByteArray())
                val drmSessionManager =
                    DefaultDrmSessionManager.Builder().setPlayClearSamplesWithoutKeys(true)
                        .setMultiSession(false).setKeyRequestParameters(HashMap())
                        .setUuidAndExoMediaDrmProvider(
                            C.CLEARKEY_UUID,
                            FrameworkMediaDrm.DEFAULT_PROVIDER
                        )
                        .build(drmCallback)
                val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36")

                val dataSourceFactoryEvent =
                    DefaultDataSource.Factory(this, httpDataSourceFactory)

                val mediaSource =
                    DefaultMediaSourceFactory(dataSourceFactoryEvent).setDrmSessionManagerProvider { drmSessionManager }
                        .createMediaSource(dashMediaItem)

                player.setMediaSource(mediaSource)
                player.prepare()
                player.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Bugsnag.notify(error)
                    }
                })
            } else {
                val mediaItem = MediaItem.fromUri(data.play_url)
                val mediaSource = HlsMediaSource.Factory(
                    DefaultHttpDataSource.Factory()
                ).createMediaSource(mediaItem)
                player.setMediaSource(mediaSource)
                player.prepare()
            }

        }
    }


    private fun loadChannel(url: ChannelLinkResponse) {
        val mediaItem =
            MediaItem.Builder().setUri(url.playUrl).setMimeType("application/dash+xml").build()
        Log.d("GG  COOKIE", "loadChannel: ${url}")
        val dataSourceFactory = DefaultHttpDataSource.Factory().setDefaultRequestProperties(
            mapOf(
                "Cookie" to "CloudFront-Policy=${url.cloudFrontPolicy}; CloudFront-Signature=${url.cloudFrontSignature}; CloudFront-Key-Pair-Id=${url.cloudFrontKeyPairId}"
            )
        )
        if (url.cloudFrontPolicy != null && url.cloudFrontSignature != null && url.cloudFrontKeyPairId != null) {
            val mediaSource = DashMediaSource.Factory(
                DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory
            ).createMediaSource(mediaItem)
            player.setMediaSource(mediaSource)

        } else {
            val mediaSource =
                DashMediaSource.Factory(SignedCookieDataSourceFactory(url.signedCookie!!))
                    .createMediaSource(MediaItem.fromUri(url.playUrl))
            player.setMediaSource(mediaSource)
        }
        player.prepare()
    }

    private fun setupUI() {
        exoPlay = binding.player.findViewById(R.id.playBtn)
        exoToggleButton = binding.player.findViewById(R.id.btn_toggle_sidebar)
        exoToggleButtonRight = binding.player.findViewById(R.id.btn_right_toggle)
        exoTitle = binding.player.findViewById(R.id.exo_anime_title)
        videoName = binding.player.findViewById(R.id.exo_video_name)
        videoInfo = binding.player.findViewById(R.id.exo_video_info)
        serverInfo = binding.player.findViewById(R.id.exo_server_info)
        exoQuality = binding.player.findViewById(R.id.exo_quality)
        exoProgress = binding.player.findViewById(com.google.android.exoplayer2.R.id.exo_progress)
        scaleBtn = binding.player.findViewById(R.id.exo_screen)
        val exoBack = binding.player.findViewById<ImageButton>(R.id.exo_back)
        val exoSpeed =
            binding.player.findViewById<ImageButton>(com.google.android.exoplayer2.R.id.exo_playback_speed)
        exoPip = binding.player.findViewById(R.id.exo_pip)

        exoProgress.setForceDisabled(true)
        exoBack.setOnClickListener {
            finishAndRemoveTask()
        }

        exoRotate = binding.player.findViewById(R.id.exo_rotate)
        exoRotate.setOnClickListener {
            toggleScreenOrientation()
        }


        exoSpeed.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DialogTheme)
            builder.setTitle("Speed")


            val speed = arrayOf("0.25", "0.5", "Normal", "1.5", "2")
            val adapter = CustomAdapter(
                this, speed
            )
            builder.setAdapter(adapter) { dad, which ->
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                hideSystemBars()

                when (which) {
                    0 -> {
                        isNormal = false
                        adapter.setSelected(0)
                        changeVideoSpeed(0.25f)
                    }

                    1 -> {
                        isNormal = false
                        adapter.setSelected(1)
                        changeVideoSpeed(0.5f)
                    }

                    2 -> {
                        isNormal = true

                        adapter.setSelected(2)
                        changeVideoSpeed(1f)
                    }

                    3 -> {
                        isNormal = false
                        adapter.setSelected(3)
                        changeVideoSpeed(1.5f)
                    }

                    else -> {
                        isNormal = false
                        adapter.setSelected(4)
                        changeVideoSpeed(2f)

                    }
                }
            }
            hideSystemBars()

            val dialog = builder.create()
            dialog.show()
        }


        exoQuality.setOnClickListener {
            initPopupQuality().show()
        }




        exoPip.setOnClickListener {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName
                ) == AppOpsManager.MODE_ALLOWED
            } else false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (status) {
                    this.enterPictureInPictureMode(
                        PictureInPictureParams.Builder().build()
                    )
                    binding.player.useController = false
                    PlayerActivity.pipStatus = false
                } else {
                    val intent = Intent(
                        "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            } else {
                Toast.makeText(
                    this, "Feature not supported on this device", Toast.LENGTH_SHORT
                ).show()
            }
        }

        exoTitle.text = selectedChannel.name
        binding.player.keepScreenOn = true

        exoPlay.setOnClickListener {
            togglePlayPause()
        }



        scaleBtn.setOnClickListener {
            if (isFullscreen < 1) isFullscreen += 1 else isFullscreen = 0
            when (isFullscreen) {
                0 -> {
                    if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                        binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    } else {
                        binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    }
                }

                1 -> {
                    binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                }
            }

            Snackbar.make(
                binding.player, (when (isFullscreen) {
                    0 -> "Original"
                    1 -> "Stretch"
                    else -> "Original"
                }), 1000
            ).show()
        }


        exoToggleButton.setOnClickListener {
            toggleSidebar(true)
            toggleSidebarRight(false)
        }
        exoToggleButtonRight.setOnClickListener {
            toggleSidebarRight(true)
            toggleSidebar(false)
        }
        binding.btnHideMenu.setOnClickListener { toggleSidebar(false) }
        binding.btnHideMenuRight.setOnClickListener { toggleSidebarRight(false) }
    }

    private fun toggleScreenOrientation() {
        requestedOrientation = when (requestedOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

            else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }


    private fun initPopupQuality(): Dialog {
        val mappedInfo = trackSelector.currentMappedTrackInfo
            ?: throw IllegalStateException("Tracks not yet available")

        val rendererIndex =
            (0 until mappedInfo.rendererCount).first { mappedInfo.getRendererType(it) == C.TRACK_TYPE_VIDEO }

        val builder = TrackSelectionDialogBuilder(
            this, "Available Qualities", trackSelector, rendererIndex
        )
        builder.setTheme(R.style.DialogTheme)
        builder.setTrackNameProvider { format ->
            if (format.frameRate > 0f) "${format.height}p @${format.frameRate.toInt()}fps"
            else "${format.height}p"
        }
        val dialog = builder.build()
        dialog.setOnDismissListener { hideSystemBars() }
        return dialog
    }

    private fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
            Glide.with(this).load(R.drawable.anim_pause_to_play).into(exoPlay)
        } else {
            player.play()
            Glide.with(this).load(R.drawable.anim_play_to_pause).into(exoPlay)
        }
    }

    private fun setupRecyclerViews() {
        categoryAdapter = PlayerCategoryAdapter { category ->
            val hasChannels = eventList.find { it.category == category.id }
            if (hasChannels == null) {
                currentCategory = category
                viewModel.loadChannelsByCategory(category.id)
            } else {
                currentCategory = category
                viewModel.loadEventChannels()
            }
        }

        channelAdapter = PlayerChannelAdapter()
        channelAdapter.setItemChannelClickListener { channel ->
            val hasChannels =
                eventList.find { it.category == currentCategory?.id && it.id == selectedChannel.id }
            if (hasChannels == null) {
                selectedChannel = channel
                viewModel.loadChannelUrl(selectedChannel.id.toInt().toString())
                toggleSidebar(false)
            } else {
                selectedChannel = channel
                setUpName()
                loadEventChannel(channel.toEventModelItem())
                toggleSidebar(false)
            }
        }

        binding.rvCategories.apply {
            layoutManager =
                LinearLayoutManager(this@LiveTvActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        binding.rvChannels.apply {
            layoutManager = LinearLayoutManager(this@LiveTvActivity)
            adapter = channelAdapter
        }

        categoryAdapter.updateCategories(categoryList)
        val defaultCategoryPosition = categoryAdapter.setDefaultSelected(currentCategory!!)
        binding.rvCategories.scrollToPosition(defaultCategoryPosition)
    }

    private fun observeViewModel() {
        viewModel.channelsData.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> updateChannelList(resource.data)
                is Resource.Loading -> showLoadingState()
                is Resource.Error -> showErrorState(resource.throwable.message)
                else -> {}
            }
        }
        viewModel.eventChannelsData.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    updateChannelEventList(resource.data)
                }

                is Resource.Loading -> {
                    showLoadingState()
                }

                is Resource.Error -> {
                    showErrorState(resource.throwable.message)
                }

                else -> {}
            }
        }
    }

    private fun updateChannelEventList(channels: List<EventModelItem>) {
        val newCategory = channels.toChannelsResponse()
        val updatedList = newCategory.filter {
            it.category == currentCategory!!.id
        }
        channelAdapter.submitList(updatedList as ArrayList<ChannelResponseItem>)
        binding.rvChannels.visible()
        binding.progressChannel.gone()
        binding.placeHolder.visibility = if (channels.isEmpty()) View.VISIBLE else View.GONE
        channelAdapter.clearSelection()
        val defaultChannelPosition = channelAdapter.setDefaultSelected(selectedChannel)
        binding.rvChannels.scrollToPosition(defaultChannelPosition)
    }

    private fun updateChannelList(channels: List<ChannelResponseItem>) {
        channelAdapter.submitList(ArrayList(channels))
        binding.rvChannels.visible()
        binding.progressChannel.gone()
        binding.placeHolder.visibility = if (channels.isEmpty()) View.VISIBLE else View.GONE
        channelAdapter.clearSelection()
        val defaultChannelPosition = channelAdapter.setDefaultSelected(selectedChannel)
        binding.rvChannels.scrollToPosition(defaultChannelPosition)
    }

    private fun showLoadingState() {
        binding.progressChannel.visible()
        binding.rvChannels.gone()
    }

    private fun showErrorState(message: String?) {
        binding.progressChannel.gone()
        snackString(message)
    }

    private fun toggleSidebar(show: Boolean) {
        binding.sidebar.animate().translationX(if (show) 0f else binding.sidebar.width.toFloat())
            .setDuration(300).start()
    }

    private fun toggleSidebarRight(show: Boolean) {
        binding.sidebarRight.animate()
            .translationX(if (show) 0f else binding.sidebarRight.width.toFloat()).setDuration(300)
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    companion object {
        private const val EXTRA_CHANNEL_DATA = "EXTRA_CHANNEL_DATA"
        var categoryList: ArrayList<ChannelCategoryItem> = arrayListOf()
        var eventList: ArrayList<EventModelItem> = arrayListOf()
        var currentCategory: ChannelCategoryItem? = null
        fun newIntent(context: Context, currentChannel: ChannelResponseItem): Intent {
            return Intent(context, LiveTvActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_DATA, currentChannel)
            }
        }
//        fun newIntent(context: Context, currentChannel: ): Intent {
//            return Intent(context, LiveTvActivity::class.java).apply {
//                putExtra(EXTRA_CHANNEL_DATA, currentChannel)
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        hideSystemBars()
    }

    override fun onPause() {
        super.onPause()
    }
}


class SignedCookieDataSourceFactory(private val signedCookie: String) : DataSource.Factory {

    private val okHttpClient = OkHttpClient()

    override fun createDataSource(): DataSource {
        return OkHttpDataSource(okHttpClient).apply {
            Log.d("GGG", "createDataSource: ${signedCookie}")
            setRequestProperty("Cookie", signedCookie)
        }
    }
}
