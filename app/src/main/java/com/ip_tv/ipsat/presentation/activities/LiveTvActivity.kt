package com.ip_tv.ipsat.presentation.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ActivityLiveTvBinding
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
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
    private lateinit var exoToggleButton: ImageView
    private lateinit var exoToggleButtonRight: ImageView
    private lateinit var exoTitle: TextView

    private var notchHeight: Int = 0
    private var isNewChannelSelected = false

    private lateinit var categoryAdapter: PlayerCategoryAdapter
    private lateinit var channelAdapter: PlayerChannelAdapter

    private lateinit var selectedChannel: ChannelResponseItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveTvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFullScreenMode()
        parseIntentData()
        setupPlayer()
        setupUI()
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
        loadChannel("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
    }

    private fun loadChannel(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    private fun setupUI() {
        exoPlay = binding.player.findViewById(R.id.playBtn)
        exoToggleButton = binding.player.findViewById(R.id.btn_toggle_sidebar)
        exoToggleButtonRight = binding.player.findViewById(R.id.btn_right_toggle)
        exoTitle = binding.player.findViewById(R.id.exo_anime_title)

        exoTitle.text = selectedChannel.name
        binding.player.keepScreenOn = true

        exoPlay.setOnClickListener {
            togglePlayPause()
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
            loadChannel(channel.scheduleListUrl)
            selectedChannel = channel
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
