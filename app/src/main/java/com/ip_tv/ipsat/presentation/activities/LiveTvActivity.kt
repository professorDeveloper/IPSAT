package com.ip_tv.ipsat.presentation.activities

import Resource
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
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
import com.ip_tv.ipsat.domain.model.ChannelCategory
import com.ip_tv.ipsat.domain.model.ChannelCategoryItem
import com.ip_tv.ipsat.domain.model.ChannelResponseItem
import com.ip_tv.ipsat.domain.model.VodMovieResponse
import com.ip_tv.ipsat.presentation.adapters.PlayerCategoryAdapter
import com.ip_tv.ipsat.presentation.adapters.PlayerChannelAdapter
import com.ip_tv.ipsat.presentation.viewmodel.LiveTvScreenViewModel
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.hideSystemBars
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.nio.channels.Channel
import kotlin.math.min

@AndroidEntryPoint
class LiveTvActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveTvBinding
    private lateinit var player: ExoPlayer
    private lateinit var exoTitle: TextView
    private lateinit var exoPlay: ImageView
    private lateinit var exoToggleButton: ImageView
    private var notchHeight: Int = 1
    private var isNewChannelSelected = false
    private val model by viewModels<LiveTvScreenViewModel>()
    private lateinit var exoProgress: PlayerActivity.ExtendedTimeBar
    private lateinit var animePlayingDetails: ChannelResponseItem

    override fun onAttachedToWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val displayCutout = window.decorView.rootWindowInsets.displayCutout
            if (displayCutout != null) {
                if (displayCutout.boundingRects.size > 0) {
                    notchHeight = min(
                        displayCutout.boundingRects[0].width(),
                        displayCutout.boundingRects[0].height()
                    )
                    checkNotch()
                }
            }
        }
        super.onAttachedToWindow()
    }

    private fun checkNotch() {
        if (notchHeight != 0) {
            val orientation = resources.configuration.orientation
            binding.player.findViewById<View>(R.id.exo_controller_cont)
                .updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        marginStart = notchHeight
                        marginEnd = notchHeight
                        topMargin = 0
                    } else {
                        topMargin = notchHeight
                        marginStart = 0
                        marginEnd = 0
                    }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveTvBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        ) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        initializePlayer()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        parseExtra()
        hideSystemBars()
        exoPlay = binding.player.findViewById(R.id.playBtn)
        exoToggleButton = binding.player.findViewById(R.id.btn_toggle_sidebar)
        exoTitle = binding.player.findViewById(R.id.exo_anime_title)
        exoProgress = findViewById(com.google.android.exoplayer2.ui.R.id.exo_progress)
        exoProgress.setForceDisabled(true)
        exoTitle.text = animePlayingDetails.name

        exoPlay.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                Glide.with(this).load(R.drawable.anim_pause_to_play).into(exoPlay)
            } else {
                player.play()
                Glide.with(this).load(R.drawable.anim_play_to_pause).into(exoPlay)
            }
        }


        setupSidebar()
    }

    private fun parseExtra() {
        animePlayingDetails =
            intent.getSerializableExtra("EXTRA_EPISODE_DATA") as ChannelResponseItem

    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.player.player = player

        loadChannel("https://minio.salomtv.uz/trailers/6c9df417-beef-4277-9a3b-8b38ce544386/master.m3u8")
    }

    private fun loadChannel(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun setupSidebar() {
        val layoutManagerCategory = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.layoutManager = layoutManagerCategory
        binding.rvChannels.layoutManager = LinearLayoutManager(this)
        val categoryPlayerAdapter = PlayerCategoryAdapter {
            model.loadChannelsByCategory(it.id)
        }
        binding.rvCategories.adapter = categoryPlayerAdapter
        categoryPlayerAdapter.updateCategories(categoryList)
        val position = categoryPlayerAdapter.setDefaultSelected(currentCategory!!)
        model.loadChannelsByCategory(currentCategory!!.id)
        layoutManagerCategory.scrollToPosition(if (position != -1) position else 0)

        model.channelsData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    val channelAdapter = PlayerChannelAdapter()
                    channelAdapter.submitList(it.data)
                    binding.rvChannels.adapter = channelAdapter
                    binding.progressChannel.gone()
                    binding.rvChannels.visible()
                    if (it.data.isEmpty()) {
                        binding.placeHolder.visible()
                    } else {
                        binding.placeHolder.gone()
                    }
                }

                is Resource.Loading -> {
                    binding.progressChannel.visible()
                    binding.rvChannels.gone()
                }

                is Resource.Error -> {
                    binding.progressChannel.gone()
                    snackString(it.throwable.message)
                }

                else -> {}
            }
        }

        exoToggleButton.setOnClickListener {
            toggleSidebar(true)
        }

        binding.btnHideMenu.setOnClickListener {
            if (!isNewChannelSelected) {
                val defaultPosition = categoryPlayerAdapter.setDefaultSelected(currentCategory!!)
                layoutManagerCategory.scrollToPosition(if (defaultPosition != -1) defaultPosition else 0)

            }
            toggleSidebar(false)
        }
    }

    private fun toggleSidebar(show: Boolean) {
        binding.sidebar.animate()
            .translationX(if (show) 0f else -binding.sidebar.width.toFloat())
            .setDuration(300)
            .start()
    }

    companion object {
        var categoryList: ArrayList<ChannelCategoryItem> = arrayListOf()
        var currentCategory: ChannelCategoryItem? = null
        fun newIntent(
            context: Context,
            currentChannel: ChannelResponseItem
        ): Intent {
            val intent = Intent(context, LiveTvActivity::class.java)
            intent.putExtra("EXTRA_EPISODE_DATA", currentChannel)
            return intent
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}