package com.example.vibeplayer.core.database.song

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val songId: Long,
    val title: String,
    val artist: String,
    val filePath: String,
    val duration: Long,
    val size: Int,
    val embeddedArt: Uri?,
    val audioUri: Uri?
)
