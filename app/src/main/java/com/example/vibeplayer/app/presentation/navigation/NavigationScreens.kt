package com.example.vibeplayer.app.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationScreens {

    @Serializable
    data object Permission : NavigationScreens

    @Serializable
    data object MainPage : NavigationScreens

    @Serializable
    data object ScanMusic : NavigationScreens

    @Serializable
    data class NowPlaying(val songId: Long = -1) : NavigationScreens
}