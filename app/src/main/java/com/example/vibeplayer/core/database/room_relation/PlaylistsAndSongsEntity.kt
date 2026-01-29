package com.example.vibeplayer.core.database.room_relation

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "id"])
data class PlaylistsAndSongsEntity(
    //PlaylistEntity.playlistId
    val playlistId: Int,
    //SongEntity.id (Room PK, not the MediaStore songId)
    val id: Int
)
