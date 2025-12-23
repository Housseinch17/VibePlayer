package com.example.vibeplayer.feature.main.presentation

import com.example.vibeplayer.core.domain.Song

data class MainPageUiState(
    val songState: SongState = SongState.Scanning,
) {
    val totalSong = if (songState is SongState.TrackList) songState.songList.size else 0
}

sealed interface SongState {
    data object Scanning : SongState
    data object Empty : SongState
    data class TrackList(val songList: List<Song>) : SongState
}
