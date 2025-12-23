package com.example.vibeplayer.core.domain

import androidx.media3.common.MediaItem

data class MediaPlayerState(
    val isPlaying: Boolean = false,
    val duration: Long = 0L,
    val currentMedia: MediaItem? = null
)
