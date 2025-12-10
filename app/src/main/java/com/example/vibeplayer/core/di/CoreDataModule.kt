package com.example.vibeplayer.core.di

import com.example.vibeplayer.core.data.SongRepositoryImpl
import com.example.vibeplayer.core.domain.SongRepository
import org.koin.dsl.module

val coreDataModule = module {
    single<SongRepository> { SongRepositoryImpl(get(), get()) }
}