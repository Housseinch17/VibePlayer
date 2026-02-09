package com.example.vibeplayer.core.database.room_relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.vibeplayer.core.database.playlist.PlaylistEntity
import com.example.vibeplayer.core.database.song.SongEntity

data class SongWithPlaylists(
    @Embedded val song: SongEntity,
    @Relation(
        parentColumn = "songDbId",
        entity = PlaylistEntity::class,
        entityColumn = "playlistId",
        associateBy = Junction(PlaylistsAndSongsEntity::class)
    )
    val playlists: List<PlaylistEntity>
)
