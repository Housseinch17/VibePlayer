package com.example.vibeplayer.core.domain

import kotlinx.coroutines.flow.Flow

interface PlaylistsWithSongsRepository {
    suspend fun createPlaylistWithSongs(
        playlistName: String,
        selectedSongIds: List<Int>
    )
    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongsDomain>>
}