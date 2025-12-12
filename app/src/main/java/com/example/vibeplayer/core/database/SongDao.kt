package com.example.vibeplayer.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM songentity")
    fun getSongs(): Flow<List<SongEntity>>

    @Upsert
    suspend fun upsertSong(song: SongEntity)

    @Delete
    suspend fun removeSong(song: SongEntity)

    @Upsert
    suspend fun upsertAll(songs: List<SongEntity>)
}