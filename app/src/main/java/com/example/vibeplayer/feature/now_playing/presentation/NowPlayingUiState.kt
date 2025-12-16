package com.example.vibeplayer.feature.now_playing.presentation

import com.example.vibeplayer.core.domain.Song

data class NowPlayingUiState(
    val song: Song = Song(),
    val isPlaying: Boolean = false,
    val progressIndicator: Float = 1f,
)
