package com.example.vibeplayer.core.database.room_relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.vibeplayer.core.database.playlist.PlaylistEntity
import com.example.vibeplayer.core.database.song.SongEntity

data class PlaylistWithSongs(
    @Embedded val playlistEntity: PlaylistEntity,
//The parentColumn always refers to a field in the parent entity (the one you @Embedded)
//The entityColumn always refers to a field in the target entity (the list you want to retrieve)
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "id",
        associateBy = Junction(PlaylistsAndSongsEntity::class)
    )
    val songs: List<SongEntity>?
)