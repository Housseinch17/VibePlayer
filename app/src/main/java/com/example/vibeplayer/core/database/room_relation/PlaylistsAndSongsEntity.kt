package com.example.vibeplayer.core.database.room_relation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.vibeplayer.core.database.song.SongEntity

@Entity(
    tableName = "PlaylistsAndSongsEntity",
    primaryKeys = ["playlistId", "songDbId"],
    //we used index songDbId because in the ForeignKey for parentColumns we have songDbId
    //and we used index playlistId because in PlaylistWithSongs we used for relation
    //parent columns the playlistId so for relation and junction we use index
    //for faster and better performance for big data1
    indices = [
        Index(value = ["songDbId"]),
        Index(value = ["playlistId"])
    ],
    //foreignKeys with onDelete = ForeignKey.CASCADE means when i delete songDbId from
    //SongEntity since entity = SongEntity::class it will be deleted from
    //PlaylistsAndSongsEntity where songDbId of PlaylistsAndSongsEntity == songDbId of SongEntity
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["songDbId"],
            childColumns = ["songDbId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistsAndSongsEntity(
    //PlaylistEntity.playlistId
    @ColumnInfo("playlistId")val playlistId: Int,
    //SongEntity.id (Room PK, not the MediaStore songDbId)
    @ColumnInfo("songDbId")val songDbId: Int
)
