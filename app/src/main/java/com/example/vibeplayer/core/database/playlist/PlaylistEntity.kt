package com.example.vibeplayer.core.database.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val playlistName: String,
)
