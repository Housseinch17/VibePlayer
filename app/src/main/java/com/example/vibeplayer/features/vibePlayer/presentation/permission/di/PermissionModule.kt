package com.example.vibeplayer.features.vibePlayer.presentation.permission.di

import com.example.vibeplayer.features.vibePlayer.presentation.permission.presentation.PermissionViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object PermissionModule {
    val permissionModule = module {
        viewModelOf(::PermissionViewModel)
    }
}