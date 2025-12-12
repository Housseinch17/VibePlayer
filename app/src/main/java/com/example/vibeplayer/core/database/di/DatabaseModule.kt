package com.example.vibeplayer.core.database.di

import androidx.room.Room
import com.example.vibeplayer.core.database.SongDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<SongDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            SongDatabase::class.java,
            "songs.db"
        ).build()
    }
    single {
        get<SongDatabase>().songDao
    }
}