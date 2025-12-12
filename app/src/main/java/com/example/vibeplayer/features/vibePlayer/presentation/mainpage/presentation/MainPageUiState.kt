package com.example.vibeplayer.features.vibePlayer.presentation.mainpage.presentation

import com.example.vibeplayer.core.data.Constants.DEFAULT_DURATION
import com.example.vibeplayer.core.data.Constants.DEFAULT_SIZE
import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data.model.Audio

data class MainPageUiState(
    val audioState: AudioState = AudioState.Scanning,
    val duration: Long = DEFAULT_DURATION,
    val size: Long = DEFAULT_SIZE
)

sealed interface AudioState{
    data object Scanning: AudioState
    data object Empty: AudioState
    data class TrackList(val audioList: List<Audio>): AudioState
}
