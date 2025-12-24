package com.example.vibeplayer.feature.search.presentation

import com.example.vibeplayer.core.domain.Song

data class SearchUiState(
    val songList: List<Song> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
