package com.example.vibeplayer.core.domain

import android.net.Uri

data class Playlist(
    val playlistId: Int = 0,
    val playlistName: String = "",
    val embeddedUri: Uri? = null
)
