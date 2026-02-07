package com.example.vibeplayer.feature.edit_playlist_songs

import com.example.vibeplayer.core.domain.Song

data class EditPlaylistSongsUiState(
    val playlistName: String = "",
    val playlistId: Int = 0,
    val existingSongs: List<Song> = emptyList()
)