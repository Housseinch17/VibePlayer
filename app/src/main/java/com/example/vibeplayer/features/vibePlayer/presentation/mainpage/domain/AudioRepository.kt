package com.example.vibeplayer.features.vibePlayer.presentation.mainpage.domain

import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data.model.Audio

interface AudioRepository {
    suspend fun getAllAudios(duration: Long, size: Long): List<Audio>
}