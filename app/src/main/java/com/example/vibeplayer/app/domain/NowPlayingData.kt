package com.example.vibeplayer.app.domain

sealed interface NowPlayingData {
    data class PlayBySongId(val id: Int) : NowPlayingData
    data object Play : NowPlayingData
    data object Shuffle : NowPlayingData
    data class PlayByPlaylist(val playlistName: String): NowPlayingData
    data class ShuffleByPlaylist(val playlistName: String): NowPlayingData
}

