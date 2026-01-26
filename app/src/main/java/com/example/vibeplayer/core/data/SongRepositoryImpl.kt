package com.example.vibeplayer.core.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import com.example.vibeplayer.core.database.song.SongDao
import com.example.vibeplayer.core.database.song.toDomainModel
import com.example.vibeplayer.core.database.song.toEntity
import com.example.vibeplayer.core.database.song.toSong
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

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

    override suspend fun scanAgain(
        duration: Long,
        size: Long,
    ): Result<List<Song>> = withContext(dispatchersIo) {
        return@withContext try {
            val songs = fetchSongs(
                duration = duration,
                size = size
            )
            val songsToEntity = songs.map {
                it.toEntity()
            }
            //remove song
            songDao.removeAllSongs()
            //reset primary key that is already generated
            songDao.deletePrimaryKeyIndex()
            songDao.upsertAll(songsToEntity)
            Result.Success(data = songs)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Result.Success(emptyList())
        }
    }

    override suspend fun getSongByMediaItem(mediaItem: MediaItem?): Song? {
        return mediaItem?.let {
            val uri = it.convertMediaItemToUri()
            songDao.getSongByUri(uri)?.toSong()
        }
    }

    override suspend fun getSongBySongId(songId: Long): Song {
        return songDao.getSongBySongId(songId).toSong()
    }

    override suspend fun getSongsByTitleOrArtistName(searchQuery: String): Flow<List<Song>> {
        if (searchQuery.isBlank()) return flowOf(emptyList())

        return songDao.getSongsByTitleOrArtistName(searchQuery.trim()).map {
            it.toDomainModel()
        }
    }

    private suspend fun fetchSongs(
        duration: Long = 0,
        size: Long = 0
    ): List<Song> = withContext(dispatchersIo) {
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

        //convert to millis since audio duration is in millis
        val durationInMillis = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.SECONDS).toString()
        //convert to Bytes since audio size is in Bytes
        val sizeInBytes = (size * 1024).toString()

        // Show only audios that are at least duration and size.
        val selection =
            "${MediaStore.Audio.Media.DURATION} >= ? AND ${MediaStore.Audio.Media.SIZE} >= ? AND ${MediaStore.Audio.Media.IS_MUSIC} = ?"

        //here the variables values to show like show duration which is at least durationInMillis...
        //for "1" here it's for IS_MUSIC 1 means true and 0 false
        val selectionArgs = arrayOf(
            durationInMillis,
            sizeInBytes,
            "1"
        )

        val sortOrder = "${MediaStore.Audio.Media.DURATION} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder

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

                val audioUri: Uri = ContentUris.withAppendedId(
                    collection,
                    id
                )

                songs.add(
                    Song(
                        id = 0,
                        songId = id,
                        title = title,
                        artist = artist,
                        filePath = filePath,
                        duration = duration,
                        size = size,
                        embeddedArt = photoUri,
                        audioUri = audioUri
                    )
                )
            }
        }
        return@withContext songs
    }
}

