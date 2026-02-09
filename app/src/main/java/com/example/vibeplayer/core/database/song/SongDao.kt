package com.example.vibeplayer.core.database.song

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

    @Query("DELETE FROM songentity WHERE songId IN (:missingSongIds)")
    suspend fun deleteMissingSongsById(missingSongIds: List<Long>)

    @Query("Select * From SongEntity where audioUri = :uri limit 1")
    suspend fun getSongByUri(uri: Uri?): SongEntity?

    @Query("Select * From SongEntity where songDbId = :id limit 1")
    suspend fun getSongById(id: Int): SongEntity

    @Query(
        "SELECT * FROM SongEntity WHERE songId NOT IN (:excludedIds)" +
                " AND (title LIKE '%' || :searchQuery || '%'\n" +
                "       OR artist LIKE '%' || :searchQuery || '%')"
    )
    suspend fun getSongsExcluding(excludedIds: List<Int>, searchQuery: String): List<SongEntity>

    @Upsert
    suspend fun upsertAll(songs: List<SongEntity>)

}