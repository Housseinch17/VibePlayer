package com.example.vibeplayer.core.data

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.vibeplayer.core.domain.PlaybackController
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class PlaybackControllerImpl(
    applicationContext: Context
) : PlaybackController {
    private val player = ExoPlayer.Builder(applicationContext).build()

    override val currentProgressIndicator: Flow<Float> = callbackFlow {
        var isPlaying = false
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }

        val job = launch {
            while (true) {
                if (isPlaying && player.duration > 0) {
                    val progress =
                        player.currentPosition.toFloat() / player.duration.toFloat()
                    trySend(progress)
                }
                delay(500)
            }
        }
        player.addListener(listener)

        awaitClose {
            job.cancel()
            player.removeListener(listener)
        }
    }

    override fun play(audioUri: Uri) {
        val mediaItem = MediaItem.fromUri(audioUri)
        //check if a media was already playing and being paused
        if (player.currentMediaItem != null && player.currentMediaItem?.localConfiguration?.uri == audioUri) {
            if (!player.isPlaying) {
                player.play()
            }
        } else {
            //different media was running
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }


    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.clearMediaItems()
        player.stop()
    }

    override fun playNext(audioUri: Uri) {
        play(audioUri = audioUri)
    }

    override fun playPrevious(audioUri: Uri) {
        play(audioUri = audioUri)
    }

}