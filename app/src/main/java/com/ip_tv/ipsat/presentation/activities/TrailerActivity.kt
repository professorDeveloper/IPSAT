package com.ip_tv.ipsat.presentation.activities
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ip_tv.ipsat.databinding.ActivityTrailerBinding
import com.ip_tv.ipsat.utils.LocalData

@UnstableApi
class TrailerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrailerBinding
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // Bind layout using ViewBinding
        binding = ActivityTrailerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // Bind PlayerView and StyledPlayerControlView
        val playerView: PlayerView = binding.playerView

        // Attach player to PlayerView
        playerView.player = player

        // Load media (MP4 or M3U8 URL)
        val mediaItem = MediaItem.fromUri(LocalData.trailerLink) // Replace with your URL
        player.setMediaItem(mediaItem)

        // Prepare and play the media
        player.prepare()
        player.play()

        // Show control view
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release player resources
        player.release()
    }
}

