package com.example.vibeplayer.core.database.room_relation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsAndSongsDao {
    @Upsert
    suspend fun upsertPlaylistSongCrossRef(crossRef: PlaylistsAndSongsEntity)

    @Transaction
    @Query("SELECT * FROM playlistentity")
    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("SELECT * FROM playlistentity where playlistName = :playlistName limit 1")
    fun getPlaylistByName(playlistName: String): Flow<PlaylistWithSongs>

    @Transaction
    @Query("SELECT * FROM songentity")
    fun getSongsWithPlaylists(): Flow<List<SongWithPlaylists>>
}