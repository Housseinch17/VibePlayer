package com.example.vibeplayer.core.data

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.Timeline
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

class PlaybackControllerImpl(
    applicationContext: Context
) : PlaybackController {
    private val player = ExoPlayer.Builder(applicationContext).build()
    private val _mediaPlayerState = MutableStateFlow(MediaPlayerState())
    override val mediaPlayerState: StateFlow<MediaPlayerState> = _mediaPlayerState.asStateFlow()

    val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _mediaPlayerState.update { newState ->
                newState.copy(currentMedia = mediaItem)
            }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            val duration = player.duration.coerceAtLeast(0L)
            _mediaPlayerState.update { newState ->
                newState.copy(
                    duration = duration
                )
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _mediaPlayerState.update { newState ->
                    newState.copy(duration = player.duration.coerceAtLeast(0L))
                }
            }
        }


        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _mediaPlayerState.update { newState ->
                newState.copy(
                    isPlaying = isPlaying
                )
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            _mediaPlayerState.update { newState ->
                newState.copy(
                    isShuffled = shuffleModeEnabled
                )
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            _mediaPlayerState.update { newState ->
                newState.copy(
                    repeatMode = repeatMode
                )
            }
        }

    }

    init {
        player.addListener(listener)
    }

    override val currentProgressIndicator: Flow<Float> = callbackFlow {
        val job = launch {
            while (true) {
                val progress = player.currentPosition.toFloat()
                trySend(progress)
                delay(500)
            }
        }

        awaitClose {
            job.cancel()
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
        //here setting the media item to the current playing media item
        //will restart it which we don't need that
        //so if the media items to be set is the same as the current media
        //just resume it
        if (player.currentMediaItemIndex != mediaItemsIndex) {
            player.seekToDefaultPosition(mediaItemsIndex)
        } else {
            if (!player.isPlaying) {
                player.play()
            }
        }
    }

    override fun currentMediaItem(): MediaItem? {
        return player.currentMediaItem
    }

    override fun play(byMediaOrder: Boolean) {
        //start from the first media in playList
        if (byMediaOrder) {
            player.shuffleModeEnabled = false
            player.seekToDefaultPosition(0)
        }
        //reaching last song and clicking play nothing will play without this condition
        if (player.playbackState == Player.STATE_ENDED) {
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

    override fun repeatMode() {
        val nextRepeatMode = when (player.repeatMode) {
            REPEAT_MODE_OFF -> REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
            REPEAT_MODE_ONE -> REPEAT_MODE_OFF
            else -> REPEAT_MODE_OFF
        }
        player.repeatMode = nextRepeatMode
    }

    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.stop()
    }

    override fun release() {
        player.release()
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