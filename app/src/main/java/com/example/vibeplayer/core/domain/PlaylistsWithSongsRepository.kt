package com.example.vibeplayer.core.domain

import com.example.vibeplayer.core.presentation.ui.UiText
import kotlinx.coroutines.flow.Flow

interface PlaylistsWithSongsRepository {
    suspend fun createPlaylistWithSongs(
        playlistName: String,
        selectedSongIds: List<Int>
    ): Result<UiText>

    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongsDomain>>

    suspend fun playlistAlreadyExists(playlistName: String): Boolean
    suspend fun saveEmptyPlaylist(playlistName: String)
    fun getPlaylistByName(playlistName: String): Flow<PlaylistWithSongsDomain>
    suspend fun isSongInFavourite(songDbId: Int): Flow<Boolean>
    suspend fun isSongAlreadyExistInPlaylist(playlistName: String,songDbId: Int): Boolean
    suspend fun addSongToPlaylist(playListName: String, songDbId: Int): Result<Unit>
    suspend fun removeSongFromPlaylist(playlistName: String, songDbId: Int): Result<Unit>
}