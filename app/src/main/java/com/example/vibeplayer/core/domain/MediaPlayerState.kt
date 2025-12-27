package com.example.vibeplayer.core.domain

import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_OFF

data class MediaPlayerState(
    val isPlaying: Boolean = false,
    val duration: Long = 0L,
    val currentMedia: MediaItem? = null,
    val isShuffled: Boolean = false,
    val repeatMode: Int = REPEAT_MODE_OFF
)
