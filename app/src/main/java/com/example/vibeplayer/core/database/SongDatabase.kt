package com.example.vibeplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vibeplayer.core.database.converters.Converters
import com.example.vibeplayer.core.database.playlist.PlaylistDao
import com.example.vibeplayer.core.database.playlist.PlaylistEntity
import com.example.vibeplayer.core.database.room_relation.PlaylistsAndSongsEntity
import com.example.vibeplayer.core.database.room_relation.PlaylistsAndSongsDao
import com.example.vibeplayer.core.database.song.SongDao
import com.example.vibeplayer.core.database.song.SongEntity

@Database(
    entities = [SongEntity::class, PlaylistEntity::class, PlaylistsAndSongsEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class SongDatabase : RoomDatabase() {
    abstract val songDao: SongDao
    abstract val playlistDao: PlaylistDao
    abstract val playlistsAndSongsDao: PlaylistsAndSongsDao
}