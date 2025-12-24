package com.example.vibeplayer.app.domain

import android.net.Uri


sealed interface NowPlayingData{
    data class PlayByUri(val uri: Uri?): NowPlayingData
    data object Play: NowPlayingData
    data object Shuffle: NowPlayingData
}