package com.example.vibeplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vibeplayer.core.database.converters.Converters

@Database(entities = [SongEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class SongDatabase : RoomDatabase() {
    abstract val songDao: SongDao
}

