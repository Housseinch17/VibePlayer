package com.example.vibeplayer.app.di

import android.content.Context
import com.example.vibeplayer.app.presentation.Application
import com.example.vibeplayer.app.presentation.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module{

    single<Context> { androidApplication().applicationContext }

    single<CoroutineScope> {
        (androidApplication() as Application).applicationScope
    }

    viewModelOf(::MainViewModel)
}