package com.example.vibeplayer.core.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.vibeplayer.core.database.SongDao
import com.example.vibeplayer.core.database.toDomainModel
import com.example.vibeplayer.core.database.toEntity
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class SongRepositoryImpl(
    private val context: Context,
    private val songDao: SongDao
) : SongRepository {
    private val dispatchersIo = Dispatchers.IO

    override suspend fun cleanUpRemovedSongs(): Result<Unit> = withContext(dispatchersIo) {
        try {
            val songs = songDao.getSongs().first()
            songs.map { songEntity ->
                if (!File(songEntity.filePath).exists()) {
                    songDao.removeSong(songEntity)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Error(exception = e)
        }
    }

    override fun getSongs(): Flow<List<Song>> {
        return songDao.getSongs().map { it.toDomainModel() }
    }

    override suspend fun syncSongsIfEmpty(): Result<Unit> = withContext(dispatchersIo) {
        if (songDao.getSongs().first().isEmpty()) {
            try {
                val songsFromDevice = fetchSongs()

                if (songsFromDevice.isNotEmpty()) {
                    val songEntities = songsFromDevice.map { it.toEntity() }
                    songDao.upsertAll(songEntities)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                Result.Error(e)
            }
        } else {
            Result.Success(Unit)
        }
    }

    override suspend fun scanAgain(): List<Song> = withContext(dispatchersIo) {
        return@withContext try {
            val songs = fetchSongs()
            val songsToEntity = songs.map {
                it.toEntity()
            }
            songDao.upsertAll(songsToEntity)
            songs
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emptyList()
        }
    }


    private suspend fun fetchSongs(): List<Song> = withContext(dispatchersIo) {
        val songs = mutableListOf<Song>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                // Get values of columns for a given audio.
                val id = cursor.getLong(idCol)
                val albumId = cursor.getLong(albumIdColumn)
                val title = cursor.getString(titleCol)
                val artist = cursor.getString(artistCol)
                val filePath = cursor.getString(dataCol)
                val duration = cursor.getLong(durationCol)
                val size = cursor.getInt(sizeCol)

                //use album id here with MediaStore for Albums and not Audio
                val photoUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    albumId
                )

                songs.add(
                    Song(
                        id = id,
                        title = title,
                        artist = artist,
                        filePath = filePath,
                        duration = duration,
                        size = size,
                        embeddedArt = photoUri
                    )
                )
            }
        }
        return@withContext songs
    }
}

