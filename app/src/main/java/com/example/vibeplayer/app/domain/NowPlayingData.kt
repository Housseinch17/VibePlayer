package com.example.vibeplayer.app.domain


sealed interface NowPlayingData{
    data class Id(val id: Int): NowPlayingData
    data object Play: NowPlayingData
    data object Shuffle: NowPlayingData
}