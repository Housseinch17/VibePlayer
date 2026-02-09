package com.example.vibeplayer.core.database.room_relation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsAndSongsDao {
    @Upsert
    suspend fun upsertPlaylistSongCrossRef(crossRef: PlaylistsAndSongsEntity)

    //prevents duplicates
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistsAndSongsEntity)

    @Transaction
    @Query("SELECT * FROM PlaylistEntity")
    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Query("SELECT EXISTS( SELECT 1 FROM PlaylistsAndSongsEntity JOIN playlistentity ON playlistentity.playlistId = PlaylistsAndSongsEntity.playlistId WHERE playlistentity.playlistName = 'Favourite' AND PlaylistsAndSongsEntity.songDbId = :songDbId)")
    fun isSongInFavourite(songDbId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM PlaylistsAndSongsEntity JOIN playlistentity ON playlistentity.playlistId = PlaylistsAndSongsEntity.playlistId WHERE playlistentity.playlistName = :playlistName AND PlaylistsAndSongsEntity.songDbId = :songDbId )")
    suspend fun isSongAlreadyExistInPlaylist(playlistName: String, songDbId: Int): Boolean

    @Query("DELETE FROM PlaylistsAndSongsEntity WHERE playlistId IN ( SELECT playlistId FROM PlaylistEntity WHERE playlistName = :playlistName)")
    suspend fun deleteSongsFromPlaylistByName(playlistName: String)

    @Query("DELETE FROM PlaylistEntity WHERE playlistName = :playlistName")
    suspend fun deletePlaylistByName(playlistName: String)

    @Query("DELETE FROM PlaylistsAndSongsEntity WHERE playlistId = :playlistId AND songDbId = :songDbId")
    suspend fun deleteSongFromPlaylist(playlistId: Int, songDbId: Int)

    @Transaction
    @Query("SELECT * FROM playlistentity where playlistName = :playlistName limit 1")
    fun getPlaylistByName(playlistName: String): Flow<PlaylistWithSongs>

    @Transaction
    @Query("SELECT * FROM songentity")
    fun getSongsWithPlaylists(): Flow<List<SongWithPlaylists>>
}