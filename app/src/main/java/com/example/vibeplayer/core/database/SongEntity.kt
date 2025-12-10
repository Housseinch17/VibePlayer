package com.example.vibeplayer.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val filePath: String,
    val duration: Long,
    val size: Int,
    val embeddedArt: ByteArray?,
)
