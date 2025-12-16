package com.example.vibeplayer.core.di

import androidx.room.Room
import com.example.vibeplayer.core.data.PlaybackControllerImpl
import com.example.vibeplayer.core.data.SongRepositoryImpl
import com.example.vibeplayer.core.database.SongDatabase
import com.example.vibeplayer.core.domain.PlaybackController
import com.example.vibeplayer.core.domain.SongRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
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

    singleOf(::SongRepositoryImpl).bind<SongRepository>()

    singleOf(::PlaybackControllerImpl).bind<PlaybackController>()
}