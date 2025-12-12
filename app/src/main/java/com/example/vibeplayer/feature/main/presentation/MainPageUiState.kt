package com.example.vibeplayer.feature.main.presentation

import com.example.vibeplayer.core.domain.Song

data class MainPageUiState(
    val songState: SongState = SongState.Scanning,
)

sealed interface SongState{
    data object Scanning: SongState
    data object Empty: SongState
    data class TrackList(val songList: List<Song>): SongState
}
