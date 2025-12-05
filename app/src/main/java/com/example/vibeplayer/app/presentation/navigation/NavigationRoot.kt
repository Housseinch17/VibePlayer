package com.example.vibeplayer.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay


@Composable
fun NavigationRoot(
    modifier: Modifier,
    startDestination: NavigationScreens
) {
    val backStack = remember { mutableStateListOf(startDestination) }
    NavDisplay(
        modifier = modifier,
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSaveableStateHolderNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator(),
        ),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                NavigationScreens.Permission -> NavEntry(key) {

                }

                NavigationScreens.MainPage -> NavEntry(key) {

                }

                NavigationScreens.NowPlaying -> NavEntry(key) {

                }

                NavigationScreens.ScanMusic -> NavEntry(key) {

                }
            }
        }
    )
}