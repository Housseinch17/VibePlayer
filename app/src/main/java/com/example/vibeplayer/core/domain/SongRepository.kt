package com.example.vibeplayer.core.domain

import androidx.media3.common.MediaItem
import com.example.vibeplayer.core.database.song.SongEntity
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    suspend fun syncSongsIfEmpty(): Result<Any>
    suspend fun cleanUpRemovedSongs(): Result<Unit>
    fun getSongs(): Flow<List<Song>>
    suspend fun scanAgain(
        duration: Long = 0,
        size: Long = 0,
    ): Result<List<Song>>

    suspend fun getSongByMediaItem(mediaItem: MediaItem?): Song?
    suspend fun getSongById(id: Int): Song
    suspend fun getSongsByTitleOrArtistName(searchQuery: String): Flow<List<Song>>
    suspend fun getSongsExcluding(excludedIds: List<Int>, searchQuery: String): List<Song>
}
