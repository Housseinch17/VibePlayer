package com.example.vibeplayer.core.database.playlist

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PlaylistEntity")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("playlistId") val playlistId: Int = 0,
    @ColumnInfo("playlistName") val playlistName: String,
    @ColumnInfo("embeddedUri") val embeddedUri: Uri? = null
)
