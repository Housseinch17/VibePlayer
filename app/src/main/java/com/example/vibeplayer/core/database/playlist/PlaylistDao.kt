package com.example.vibeplayer.core.database.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaylistDao {
    //note here insert because it returns the generated id while
    //upsert doesn't return generated id
    @Insert
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Query("SELECT EXISTS(SELECT 1 FROM playlistentity WHERE playlistName = :playlistName)")
    suspend fun playlistAlreadyExists(playlistName: String): Boolean

    @Query("SELECT * FROM playlistentity WHERE playlistName = :name LIMIT 1")
    suspend fun getPlaylistByName(name: String): PlaylistEntity?

}