package com.example.vibeplayer.core.domain

import kotlinx.coroutines.flow.Flow

interface SongRepository {
    suspend fun syncSongsIfEmpty(): Result<Any>
    suspend fun cleanUpRemovedSongs(): Result<Unit>
    fun getSongs(): Flow<List<Song>>
    suspend fun scanAgain(): List<Song>

}
