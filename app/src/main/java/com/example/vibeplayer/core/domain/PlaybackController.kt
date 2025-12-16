package com.example.vibeplayer.core.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface PlaybackController {
    val currentProgressIndicator: Flow<Float>
    fun play(audioUri: Uri)
    fun pause()
    fun stop()
    fun playNext(audioUri: Uri)
    fun playPrevious(audioUri: Uri)
}