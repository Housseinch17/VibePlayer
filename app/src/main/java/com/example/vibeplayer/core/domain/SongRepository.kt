package com.example.vibeplayer.core.domain

import kotlinx.coroutines.flow.Flow

interface SongRepository {
    suspend fun syncSongsIfEmpty(): Result<Any>
    suspend fun cleanUpRemovedSongs(): Result<Unit>
    fun getSongs(): Flow<List<Song>>
    suspend fun scanAgain(
        duration: Long = 0,
        size: Long = 0,
    ): Result<List<Song>>

    suspend fun getSongById(id: Int): Song
    suspend fun getPreviousSong(previousId: Int): Song?
    suspend fun getNextSong(nextId: Int): Song?
}
