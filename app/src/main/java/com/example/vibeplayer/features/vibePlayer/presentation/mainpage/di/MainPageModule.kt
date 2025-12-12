package com.example.vibeplayer.features.vibePlayer.presentation.mainpage.di

import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data.AudioImplementation
import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data.AudioManager
import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.domain.AudioRepository
import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.presentation.MainPageViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object MainPageModule {
    val mainPageModule = module {
        singleOf(::AudioManager)
        singleOf(::AudioImplementation).bind<AudioRepository>()
        viewModelOf(::MainPageViewModel)
    }
}