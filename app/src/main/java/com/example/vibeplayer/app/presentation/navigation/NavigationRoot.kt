package com.example.vibeplayer.app.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.vibeplayer.core.presentation.ui.ObserveAsEvents
import com.example.vibeplayer.feature.main.presentation.MainPageEvents
import com.example.vibeplayer.feature.main.presentation.MainPageScreen
import com.example.vibeplayer.feature.main.presentation.MainViewModel
import com.example.vibeplayer.feature.permission.presentation.PermissionEvents
import com.example.vibeplayer.feature.permission.presentation.PermissionScreen
import com.example.vibeplayer.feature.permission.presentation.PermissionViewModel
import org.koin.androidx.compose.koinViewModel


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
                    val permissionViewModel = koinViewModel<PermissionViewModel>()
                    val permissionUiState by permissionViewModel.permissionUiState.collectAsStateWithLifecycle()

                    ObserveAsEvents(permissionViewModel.permissionEvents) { permissionEvents ->
                        when (permissionEvents) {
                            PermissionEvents.NavigateMainPage -> {
                                backStack.remove(NavigationScreens.Permission)
                                backStack.add(NavigationScreens.MainPage)
                            }

                        }
                    }

                    PermissionScreen(
                        modifier = Modifier.fillMaxSize(),
                        permissionUiState = permissionUiState,
                        onActions = permissionViewModel::onActions
                    )
                }

                NavigationScreens.MainPage -> NavEntry(key) {
                    val mainViewModel = koinViewModel<MainViewModel>()
                    val mainUiState by mainViewModel.mainPageUiState.collectAsStateWithLifecycle()

                    ObserveAsEvents(mainViewModel.mainPageEvents) { events ->
                        when (events) {
                            MainPageEvents.NavigateToScanMusic -> backStack.add(NavigationScreens.ScanMusic)
                            is MainPageEvents.NavigateToNowPlaying -> backStack.add(
                                NavigationScreens.NowPlaying(events.songId)
                            )
                        }
                    }
                    MainPageScreen(
                        modifier = Modifier.fillMaxSize(),
                        mainPageUiState = mainUiState,
                        onActions = mainViewModel::onActions
                    )
                }

                is NavigationScreens.NowPlaying -> NavEntry(key) {

                }

                NavigationScreens.ScanMusic -> NavEntry(key) {

                }
            }
        }
    )
}