package com.example.vibeplayer.core.domain

import androidx.media3.common.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlaybackController {
    val currentProgressIndicator: Flow<Float>
    val mediaPlayerState: StateFlow<MediaPlayerState>

    fun setPlayList(songList: List<Song>)
    fun setMediaItemByIndex(mediaItemsIndex: Int)
    fun currentMediaItem(): MediaItem?
    fun play(byMediaOrder: Boolean = false)
    fun shuffle()
    fun repeatMode()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(position: Long)
    fun seekToNext()
    fun seekToPrevious()
}