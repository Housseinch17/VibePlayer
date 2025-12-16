package com.example.vibeplayer.core.domain

import android.net.Uri

data class Song(
    val id: Int = 0,
    val songId: Long = 0,
    val title: String = "",
    val artist: String = "",
    val filePath: String = "",
    val duration: Long = 0,
    val size: Int = 0,
    val embeddedArt: Uri? = null,
    val audioUri: Uri? = null
)
