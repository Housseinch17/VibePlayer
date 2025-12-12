package com.example.vibeplayer.core.di

import com.example.vibeplayer.core.data.SongRepositoryImpl
import com.example.vibeplayer.core.domain.SongRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::SongRepositoryImpl).bind<SongRepository>()
}