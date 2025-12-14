package com.example.vibeplayer.feature.scan_music.di

import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val scanMusicModule = module{
    viewModelOf(::ScanMusicViewModel)

}