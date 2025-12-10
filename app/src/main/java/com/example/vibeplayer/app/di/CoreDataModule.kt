package com.example.vibeplayer.app.di

import com.example.vibeplayer.app.data.SongRepositoryImpl
import com.example.vibeplayer.app.domain.SongRepository
import org.koin.dsl.module

val coreDataModule = module {
    single<SongRepository> { SongRepositoryImpl(get(), get()) }
}