package com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data.model

import android.net.Uri

// Container for information about each audio.
data class Audio(
    val id: Long,
    val title: String,
    val artist: String,
    val filePath: String,
    val duration: Long,
    val size: Int,
    val photoUri: Uri
)
