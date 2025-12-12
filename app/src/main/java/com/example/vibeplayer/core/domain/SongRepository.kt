package com.example.vibeplayer.core.domain

import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun syncSongsIfEmpty(): Flow<Result<Unit>>

    fun getSongs(): Flow<List<Song>>


}
