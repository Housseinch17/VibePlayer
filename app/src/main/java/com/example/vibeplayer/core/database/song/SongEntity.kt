package com.example.vibeplayer.core.database.song

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SongEntity",
    //songId should be unique and here we used songDbId as Primary key because setMediaItemsByIndex
    //should be set by the index of list and can't use songId to pick from the list
    indices = [Index(value = ["songId"], unique = true)]
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("songDbId") val songDbId: Int,
    @ColumnInfo("songId") val songId: Long,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("artist") val artist: String,
    @ColumnInfo("filePath") val filePath: String,
    @ColumnInfo("duration") val duration: Long,
    @ColumnInfo("size") val size: Int,
    @ColumnInfo("embeddedArt") val embeddedArt: Uri?,
    @ColumnInfo("audioUri") val audioUri: Uri?
)
