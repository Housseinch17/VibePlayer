package com.example.vibeplayer.feature.permission.di

import com.example.vibeplayer.feature.permission.presentation.PermissionViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val permissionModule = module {
    viewModelOf(::PermissionViewModel)
}