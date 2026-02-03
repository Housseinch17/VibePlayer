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

    @Transaction
    @Query("SELECT * FROM playlistentity")
    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Query("SELECT EXISTS( SELECT 1 FROM playlistsandsongsentity JOIN playlistentity ON playlistentity.playlistId = playlistsandsongsentity.playlistId WHERE playlistentity.playlistName = 'Favourite' AND playlistsandsongsentity.id = :songDbId)")
    fun isSongInFavourite(songDbId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM playlistsandsongsentity JOIN playlistentity ON playlistentity.playlistId = playlistsandsongsentity.playlistId WHERE playlistentity.playlistName = :playlistName AND playlistsandsongsentity.id = :songDbId )")
    suspend fun isSongAlreadyExistInPlaylist(playlistName: String,songDbId: Int): Boolean

    //prevents duplicates
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistsAndSongsEntity)

    @Query("DELETE FROM playlistsandsongsentity WHERE playlistId = :playlistId AND id = :songDbId")
    suspend fun deleteSongFromPlaylist(playlistId: Int, songDbId: Int)

    @Transaction
    @Query("SELECT * FROM playlistentity where playlistName = :playlistName limit 1")
    fun getPlaylistByName(playlistName: String): Flow<PlaylistWithSongs>

    @Transaction
    @Query("SELECT * FROM songentity")
    fun getSongsWithPlaylists(): Flow<List<SongWithPlaylists>>
}