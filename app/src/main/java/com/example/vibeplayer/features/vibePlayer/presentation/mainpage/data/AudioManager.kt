package com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data.model.Audio
import java.util.concurrent.TimeUnit

class AudioManager(
    private val applicationContext: Context
) {
    fun fetchAllAudios(
        duration: Long,
        size: Long,
    ): List<Audio> {
        //check google documents for more information
        //https://developer.android.com/training/data-storage/shared/media#other-file-types
        //github:
        //https://github.com/android/storage-samples/blob/main/MediaStore/app/src/main/java/com/android/samples/mediastore/MainActivityViewModel.kt

        val audioList = mutableListOf<Audio>()

        // Add a specific media item.
        val resolver = applicationContext.contentResolver

        // Find all audio files on the primary external storage device.
        val audioCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
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
            "${MediaStore.Audio.Media.DURATION} >= ? And ${MediaStore.Audio.Media.SIZE} >= ?"
        val selectionArgs = arrayOf(
            durationInMillis,
            sizeInBytes
        )

        // Display audios in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val query = resolver.query(
            audioCollection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                // Get values of columns for a given audio.
                val id = cursor.getLong(idColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val filePath = cursor.getString(dataColumn)
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getInt(sizeColumn)

                //use album id here with MediaStore for Albums and not Audio
                val photoUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    albumId
                )

                // Stores column values and the photoUri in a local object
                // that represents the media file.
                audioList += Audio(
                    id = id,
                    title = title,
                    artist = artist,
                    filePath = filePath,
                    duration = duration,
                    size = size,
                    photoUri = photoUri
                )
            }
        }
        return audioList
    }
}