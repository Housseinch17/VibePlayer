package com.example.vibeplayer.core.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.domain.SongRepository
import com.example.vibeplayer.core.database.SongDao
import com.example.vibeplayer.core.database.toDomainModel
import com.example.vibeplayer.core.database.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class SongRepositoryImpl(
    private val context: Context,
    private val songDao: SongDao
) : SongRepository {


    override fun getSongs(): Flow<List<Song>> {
        return songDao.getSongs().map { it.toDomainModel() }
    }

    override fun syncSongsIfEmpty(): Flow<Result<Unit>> = flow {
        if (songDao.getSongs().first().isEmpty()) {
            try {
                emit(Result.Loading)
                val songsFromDevice = fetchSongs()
                val songEntities = songsFromDevice.map { it.toEntity() }

                if (songEntities.isNotEmpty()) {
                    songDao.upsertAll(songEntities)
                }
                emit(Result.Success(Unit))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        } else {
            emit(Result.Success(Unit))
        }
    }.flowOn(Dispatchers.IO)

    fun fetchSongs(): List<Song> {
        val songs = mutableListOf<Song>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
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
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val retriever = MediaMetadataRetriever()
                var artBytes: ByteArray? = null
                try {
                    retriever.setDataSource(cursor.getString(dataCol))
                    artBytes = retriever.embeddedPicture
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    retriever.release()
                }

                songs.add(
                    Song(
                        id = cursor.getLong(idCol),
                        title = cursor.getString(titleCol),
                        artist = cursor.getString(artistCol),
                        filePath = cursor.getString(dataCol),
                        duration = cursor.getLong(durationCol),
                        size = cursor.getInt(sizeCol),
                        embeddedArt = artBytes
                    )
                )
            }
        }
        return songs
    }
}

