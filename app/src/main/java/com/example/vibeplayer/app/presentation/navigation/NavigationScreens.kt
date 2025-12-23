package com.example.vibeplayer.app.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.example.vibeplayer.app.domain.NowPlayingData
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationScreens: NavKey {
    @Serializable
    data object Permission : NavigationScreens

    @Serializable
    data object MainPage : NavigationScreens

    @Serializable
    data object ScanMusic : NavigationScreens

    @Serializable
    data class NowPlaying(val nowPlayingData: NowPlayingData) : NavigationScreens

    @Serializable
    data object Search: NavigationScreens
}