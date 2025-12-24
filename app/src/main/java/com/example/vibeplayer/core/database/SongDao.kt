package com.example.vibeplayer.core.database

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM songentity")
    fun getSongs(): Flow<List<SongEntity>>

    @Delete
    suspend fun removeSong(song: SongEntity)

    @Query("DELETE FROM songentity")
    suspend fun removeAllSongs()

    @Query(
        """
    SELECT * FROM SongEntity
    WHERE title LIKE '%' || :searchQuery || '%'
       OR artist LIKE '%' || :searchQuery || '%'
"""
    )
    fun getSongsByTitleOrArtistName(searchQuery: String): Flow<List<SongEntity>>

    //used to reset the primary key that is auto generated, check example below:
    //https://medium.com/@sdevpremthakur/how-to-reset-room-db-completely-including-primary-keys-android-6382f00df87b
    @Query("DELETE FROM sqlite_sequence WHERE name = 'SongEntity'")
    suspend fun deletePrimaryKeyIndex()

    @Query("Select * From SongEntity where audioUri = :uri limit 1")
    suspend fun getSongByUri(uri: Uri?): SongEntity?

    @Upsert
    suspend fun upsertAll(songs: List<SongEntity>)

}