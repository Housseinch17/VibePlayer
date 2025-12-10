package com.example.vibeplayer.feature.main

import com.example.vibeplayer.core.domain.Song

data class MainState (
    val isLoading: Boolean = true,
    val songs: List<Song>? = null
)