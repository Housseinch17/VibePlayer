package com.example.vibeplayer.feature.now_playing.di

import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val nowPlayingModule = module {
    viewModelOf(::NowPlayingViewModel)
}