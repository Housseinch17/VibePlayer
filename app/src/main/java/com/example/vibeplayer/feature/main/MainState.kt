package com.example.vibeplayer.feature.main

import com.example.vibeplayer.app.domain.Song

data class MainState (
    val isLoading: Boolean = true,
    val songs: List<Song>? = null
)