package com.example.vibeplayer.feature.main.di

import com.example.vibeplayer.feature.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {
    viewModelOf(::MainViewModel)
}