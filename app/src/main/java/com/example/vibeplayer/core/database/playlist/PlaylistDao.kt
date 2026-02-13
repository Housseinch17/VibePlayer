package com.example.vibeplayer.core.database.playlist

import android.net.Uri
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PlaylistDao {
    //note here insert because it returns the generated id while
    //upsert doesn't return generated id
    @Insert
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Query("SELECT EXISTS(SELECT 1 FROM PlaylistEntity WHERE playlistName = :playlistName)")
    suspend fun playlistAlreadyExists(playlistName: String): Boolean

    @Query("SELECT * FROM PlaylistEntity WHERE playlistName = :name LIMIT 1")
    suspend fun getPlaylistByName(name: String): PlaylistEntity?

    @Query("SELECT playlistId FROM PlaylistEntity WHERE playlistName = :name LIMIT 1")
    suspend fun getPlaylistIdByName(name: String): Int

    @Upsert
    suspend fun renamePlaylistName(playlistEntity: PlaylistEntity)

    @Query("UPDATE PlaylistEntity SET embeddedUri = NULL WHERE playlistId = :playlistId")
    suspend fun deleteCoverPhotoById(playlistId: Int)
    @Upsert
    suspend fun changeCover(playlistEntity: PlaylistEntity)

    @Query("SELECT embeddedUri from PlaylistEntity WHERE playlistName = :playlistName LIMIT 1")
    suspend fun getCoverImage(playlistName: String): Uri?


}