package com.example.vibeplayer.app.domain

sealed interface NowPlayingData {
    data class PlayBySongId(val songId: Long) : NowPlayingData
    data object Play : NowPlayingData
    data object Shuffle : NowPlayingData
}

