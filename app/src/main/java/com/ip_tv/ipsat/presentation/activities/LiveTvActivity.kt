package com.ip_tv.ipsat.presentation.activities

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.snackbar.Snackbar
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ActivityLiveTvBinding
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
import com.ip_tv.ipsat.domain.model.ChannelLinkResponse
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.presentation.adapters.PlayerCategoryAdapter
import com.ip_tv.ipsat.presentation.adapters.PlayerChannelAdapter
import com.ip_tv.ipsat.presentation.viewmodel.LiveTvScreenViewModel
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.hideSystemBars
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min

@AndroidEntryPoint
class LiveTvActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveTvBinding
    private lateinit var player: ExoPlayer
    private val viewModel by viewModels<LiveTvScreenViewModel>()

    private lateinit var exoPlay: ImageView
    private lateinit var exoQuality: ImageButton
    private lateinit var exoRotate: ImageButton
    private lateinit var exoPip: ImageButton
    private lateinit var scaleBtn: ImageButton

    private lateinit var exoToggleButton: ImageView
    private lateinit var exoToggleButtonRight: ImageView
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveTvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFullScreenMode()
        parseIntentData()
        setupPlayer()
        setupUI()
        viewModel.loadChannelsByCategory(currentCategory!!.id)
        viewModel.loadChannelUrl(selectedChannel.id.toInt().toString())
        setupRecyclerViews()
        observeViewModel()
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

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            binding.player.player = this
            playWhenReady = true
        }
        viewModel.animeLink.observe(this) {
            setUpName(it)
            loadChannel(it)
        }
    }

    private fun setUpName(url: ChannelLinkResponse) {
        exoTitle.text = selectedChannel.name
        serverInfo.text = selectedChannel.name
        videoInfo.text = url.objectKey
        videoName.text = url.objectKey
    }

    private fun loadChannel(url: ChannelLinkResponse) {
        val mediaItem = MediaItem.Builder()
            .setUri(url.playUrl)
            .setMimeType("application/dash+xml")
            .build()
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(
                mapOf(
                    "Cookie" to "CloudFront-Policy=${url.cloudFrontPolicy}; CloudFront-Signature=${url.cloudFrontSignature}; CloudFront-Key-Pair-Id=${url.cloudFrontKeyPairId}"
                )
            )


        val mediaSource: MediaSource = DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)

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
        exoRotate = binding.player.findViewById(R.id.exo_rotate)
        exoQuality = binding.player.findViewById(R.id.exo_quality)
        exoProgress = binding.player.findViewById(com.google.android.exoplayer2.R.id.exo_progress)
        scaleBtn = binding.player.findViewById(R.id.exo_screen)
        exoPip = binding.player.findViewById(R.id.exo_pip)

        exoProgress.setForceDisabled(true)

        var flag = true
        exoRotate.setOnClickListener {
            if (flag) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                flag = false
            } else {
                this.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                binding.player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                flag = true

            }
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
                    this,
                    "Feature not supported on this device",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        exoTitle.text = selectedChannel.name
        binding.player.keepScreenOn = true

        exoPlay.setOnClickListener {
            togglePlayPause()
        }

        var locked = false
        val container = binding.player.findViewById<View>(R.id.exo_controller_cont)
        val screen = binding.player.findViewById<View>(R.id.exo_black_screen)
        val lockButton = binding.player.findViewById<ImageButton>(R.id.exo_unlock)
        binding.player.findViewById<ImageButton>(R.id.exo_lock).setOnClickListener {
            locked = true
            screen.visibility = View.GONE
            container.visibility = View.GONE
            lockButton.visibility = View.VISIBLE
        }
        lockButton.setOnClickListener {
            locked = false
            screen.visibility = View.VISIBLE
            container.visibility = View.VISIBLE
            it.visibility = View.GONE
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
                binding.player, (
                        when (isFullscreen) {
                            0 -> "Original"
                            1 -> "Stretch"
                            else -> "Original"
                        }
                        ), 1000
            ).show()
        }


        exoToggleButton.setOnClickListener { toggleSidebar(true) }
        exoToggleButtonRight.setOnClickListener { toggleSidebarRight(true) }
        binding.btnHideMenu.setOnClickListener { toggleSidebar(false) }
        binding.btnHideMenuRight.setOnClickListener { toggleSidebarRight(false) }
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
            viewModel.loadChannelsByCategory(category.id)
        }

        channelAdapter = PlayerChannelAdapter()
        channelAdapter.setItemChannelClickListener { channel ->
            selectedChannel = channel
            viewModel.loadChannelUrl(selectedChannel.id.toInt().toString())
            toggleSidebar(false)
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
        binding.sidebar.animate()
            .translationX(if (show) 0f else -binding.sidebar.width.toFloat())
            .setDuration(300)
            .start()
    }

    private fun toggleSidebarRight(show: Boolean) {
        binding.sidebarRight.animate()
            .translationX(if (show) 0f else binding.sidebarRight.width.toFloat())
            .setDuration(300)
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    companion object {
        private const val EXTRA_CHANNEL_DATA = "EXTRA_CHANNEL_DATA"
        var categoryList: ArrayList<ChannelCategoryItem> = arrayListOf()
        var currentCategory: ChannelCategoryItem? = null

        fun newIntent(context: Context, currentChannel: ChannelResponseItem): Intent {
            return Intent(context, LiveTvActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_DATA, currentChannel)
            }
        }
    }
}
