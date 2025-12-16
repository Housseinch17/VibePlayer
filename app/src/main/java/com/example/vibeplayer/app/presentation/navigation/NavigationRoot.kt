package com.example.vibeplayer.app.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.vibeplayer.core.presentation.ui.ObserveAsEvents
import com.example.vibeplayer.feature.main.presentation.MainPageEvents
import com.example.vibeplayer.feature.main.presentation.MainPageScreen
import com.example.vibeplayer.feature.main.presentation.MainViewModel
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingEvents
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingViewModel
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingScreen
import com.example.vibeplayer.feature.permission.presentation.PermissionEvents
import com.example.vibeplayer.feature.permission.presentation.PermissionScreen
import com.example.vibeplayer.feature.permission.presentation.PermissionViewModel
import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicEvents
import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicScreen
import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun NavigationRoot(
    modifier: Modifier,
    startDestination: NavigationScreens
) {
    val backStack = rememberNavBackStack(startDestination)
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
        entryProvider = entryProvider {
            entry<NavigationScreens.Permission> {
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

            entry<NavigationScreens.MainPage> {
                val mainViewModel = koinViewModel<MainViewModel>()
                val mainUiState by mainViewModel.mainPageUiState.collectAsStateWithLifecycle()

                ObserveAsEvents(mainViewModel.mainPageEvents) { events ->
                    when (events) {
                        MainPageEvents.NavigateToScanMusic -> backStack.add(NavigationScreens.ScanMusic)
                        is MainPageEvents.NavigateToNowPlaying -> {
                            backStack.add(
                                NavigationScreens.NowPlaying(events.id)
                            )
                        }
                    }
                }

                MainPageScreen(
                    modifier = Modifier.fillMaxSize(),
                    mainPageUiState = mainUiState,
                    onActions = mainViewModel::onActions
                )
            }

            entry<NavigationScreens.ScanMusic> {
                val scanMusicViewModel = koinViewModel<ScanMusicViewModel>()
                val scanMusicUi by scanMusicViewModel.scanMusicUi.collectAsStateWithLifecycle()

                ObserveAsEvents(scanMusicViewModel.scanMusicEvents) { events ->
                    when (events) {
                        //backstack.removeLastOrNull() similar to navigateUp()
                        ScanMusicEvents.NavigateBack -> backStack.removeLastOrNull()
                    }
                }
                ScanMusicScreen(
                    modifier = Modifier.fillMaxSize(),
                    scanMusicUi = scanMusicUi,
                    onActions = scanMusicViewModel::onActions
                )
            }
            entry<NavigationScreens.NowPlaying> { key ->
                //here to use the key in viewmodel we have to pass it in parametersOf
                val nowPlayingViewModel = koinViewModel<NowPlayingViewModel> {
                    parametersOf(key)
                }
                val nowPlayingUiState by nowPlayingViewModel.nowPlayingUiState.collectAsStateWithLifecycle()

                ObserveAsEvents(nowPlayingViewModel.nowPlayingEvents) { events ->
                    when (events) {
                        NowPlayingEvents.NavigateBack -> backStack.removeLastOrNull()
                    }
                }

                NowPlayingScreen(
                    modifier = Modifier.fillMaxSize(),
                    nowPlayingUiState = nowPlayingUiState,
                    nowPlayingActions = nowPlayingViewModel::onActions
                )
            }
        }
    )
}