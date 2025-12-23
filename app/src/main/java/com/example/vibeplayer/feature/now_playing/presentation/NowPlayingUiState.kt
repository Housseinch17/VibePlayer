package com.example.vibeplayer.feature.now_playing.presentation

import com.example.vibeplayer.core.domain.MediaPlayerState
import com.example.vibeplayer.core.domain.Song

data class NowPlayingUiState(
    val song: Song = Song(),
    val progressIndicator: Float = 0f,
    val mediaPlayerState: MediaPlayerState = MediaPlayerState()
)
