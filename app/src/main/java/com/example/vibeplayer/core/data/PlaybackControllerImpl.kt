package com.example.vibeplayer.core.data

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.vibeplayer.core.domain.MediaPlayerState
import com.example.vibeplayer.core.domain.PlaybackController
import com.example.vibeplayer.core.domain.Song
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaybackControllerImpl(
    applicationContext: Context
) : PlaybackController {
    private val player = ExoPlayer.Builder(applicationContext).build()
    private val _mediaPlayerState = MutableStateFlow(MediaPlayerState())
    override val mediaPlayerState: StateFlow<MediaPlayerState> = _mediaPlayerState.asStateFlow()
    val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            with(player) {
                _mediaPlayerState.update { newState ->
                    newState.copy(
                        isPlaying = isPlaying,
                        duration = duration.coerceAtLeast(0L),
                        currentMedia = player.currentMediaItem
                    )
                }
            }
        }
    }

    override val currentProgressIndicator: Flow<Float> = callbackFlow {
        val job = launch {
            while (true) {
                if (player.isPlaying && player.duration > 0) {
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

    override fun setPlayList(songList: List<Song>) {
        val mediaItems: List<MediaItem> = songList.mapNotNull {
            it.audioUri?.let { audioUri ->
                MediaItem.fromUri(audioUri)
            }
        }
        if (songList.isNotEmpty()) {
            player.setMediaItems(mediaItems, true)
            player.prepare()
        }
    }

    override fun setMediaItemByIndex(mediaItemsIndex: Int) {
        player.seekToDefaultPosition(mediaItemsIndex)
    }

    override fun play(byMediaOrder: Boolean) {
        //start from the first media in playList
        if(byMediaOrder){
            player.seekToDefaultPosition(0)
        }
        //reaching last song and clicking play nothing will play without this condition
        if (player.playbackState == Player.STATE_ENDED) {
            Timber.tag("MyTag").d("ENDED")
            player.seekToDefaultPosition()
        }
        //after using stop() it will enter STATE_IDLE so if we try to play without prepare() nothing will work
        //seekToDefaultPosition(0) will start from the first of the list
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        player.play()
    }

    override fun shuffle() {
        if (player.mediaItemCount == 0) return
        player.shuffleModeEnabled = true
        val randomIndex = (0 until player.mediaItemCount).random()
        player.seekToDefaultPosition(randomIndex)
        //after using stop() we have to prepare again
        //because using stop while media1 was running without the prepare() again it will
        //not run the seekToDefaultPosition() the old one will run
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.stop()
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun seekToNext() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
            player.play()
        }
    }

    override fun seekToPrevious() {
        if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
            player.play()
        }
    }
}