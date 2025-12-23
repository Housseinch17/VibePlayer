package com.example.vibeplayer.core.database

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.vibeplayer.core.domain.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM songentity")
    fun getSongs(): Flow<List<SongEntity>>

    @Upsert
    suspend fun upsertSong(song: SongEntity)

    @Delete
    suspend fun removeSong(song: SongEntity)

    @Query("DELETE FROM songentity")
    suspend fun removeAllSongs()

    @Query("Select * From SongEntity where audioUri = :uri limit 1")
    suspend fun getSongByUri(uri: Uri?): Song?

    @Upsert
    suspend fun upsertAll(songs: List<SongEntity>)

    @Query("SELECT * FROM songentity WHERE id = :id LIMIT 1")
    suspend fun getSongById(id: Int): SongEntity
}