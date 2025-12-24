package com.example.vibeplayer.core.domain

import android.net.Uri
import androidx.media3.common.MediaItem
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
    suspend fun getSongByUri(uri: Uri?): Song?
    suspend fun getSongsByTitleOrArtistName(searchQuery: String): Flow<List<Song>>
}
