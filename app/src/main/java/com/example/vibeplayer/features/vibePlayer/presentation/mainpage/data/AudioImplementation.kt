package com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data

import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data.model.Audio
import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.domain.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioImplementation(
    private val audioManager: AudioManager
) : AudioRepository {
    override suspend fun getAllAudios(duration: Long, size: Long): List<Audio> =
        withContext(Dispatchers.IO) {
            return@withContext audioManager.fetchAllAudios(duration = duration, size = size)
        }
}