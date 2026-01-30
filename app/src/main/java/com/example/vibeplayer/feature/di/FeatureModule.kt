package com.example.vibeplayer.feature.di

import com.example.vibeplayer.feature.add_songs.AddSongsViewModel
import com.example.vibeplayer.feature.main.presentation.MainViewModel
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingViewModel
import com.example.vibeplayer.feature.permission.presentation.PermissionViewModel
import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicViewModel
import com.example.vibeplayer.feature.search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val featureModule = module{
    viewModelOf(::MainViewModel)
    viewModelOf(::NowPlayingViewModel)
    viewModelOf(::PermissionViewModel)
    viewModelOf(::ScanMusicViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::AddSongsViewModel)
}