package com.example.vibeplayer.app.navigation

import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.vibeplayer.core.presentation.ui.ObserveAsEvents
import com.example.vibeplayer.feature.add_songs.AddSongsEvents
import com.example.vibeplayer.feature.add_songs.AddSongsScreen
import com.example.vibeplayer.feature.add_songs.AddSongsViewModel
import com.example.vibeplayer.feature.main.presentation.MainPageEvents
import com.example.vibeplayer.feature.main.presentation.MainPageScreen
import com.example.vibeplayer.feature.main.presentation.MainViewModel
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingEvents
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingScreen
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingUiState
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingViewModel
import com.example.vibeplayer.feature.permission.presentation.PermissionEvents
import com.example.vibeplayer.feature.permission.presentation.PermissionScreen
import com.example.vibeplayer.feature.permission.presentation.PermissionViewModel
import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicEvents
import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicScreen
import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicViewModel
import com.example.vibeplayer.feature.search.presentation.SearchEvents
import com.example.vibeplayer.feature.search.presentation.SearchScreen
import com.example.vibeplayer.feature.search.presentation.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun NavigationRoot(
    modifier: Modifier,
    startDestination: NavigationScreens,
) {
    val backStack = rememberNavBackStack(startDestination)

    var isMinimized by rememberSaveable {
        mutableStateOf(false)
    }

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
                            backStack.add(NavigationScreens.MainPage)
                            backStack.remove(NavigationScreens.Permission)
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

                val context = LocalContext.current
                ObserveAsEvents(mainViewModel.mainPageEvents) { events ->
                    when (events) {
                        MainPageEvents.NavigateToScanMusic -> backStack.add(NavigationScreens.ScanMusic)
                        is MainPageEvents.NavigateToNowPlaying -> {
                            backStack.add(
                                NavigationScreens.NowPlaying(events.nowPlayingData)
                            )
                        }

                        MainPageEvents.NavigateToSearch -> backStack.add(NavigationScreens.Search)
                        is MainPageEvents.NavigateToAddSongs -> {
                            backStack.add(
                                NavigationScreens.AddSong(
                                    events.playlistName
                                )
                            )
                        }

                        is MainPageEvents.ShowToast -> Toast.makeText(
                            context,
                            events.message.asString(context = context),
                            Toast.LENGTH_LONG
                        ).show()

                        is MainPageEvents.NavigateToPlaylist -> {

                        }
                    }
                }

                MainPageScreen(
                    modifier = Modifier.fillMaxSize(),
                    mainPageUiState = mainUiState,
                    onActions = mainViewModel::onActions,
                    isMinimized = isMinimized
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
            entry<NavigationScreens.NowPlaying>(
                metadata = NavDisplay.transitionSpec {
                    // Slide new content up, keeping the old content in place underneath
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(1000)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                } + NavDisplay.popTransitionSpec {
                    // Slide old content down, revealing the new content in place underneath
                    EnterTransition.None togetherWith
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(1000)
                            )
                }
            ) { key ->
                //here to use the key in viewmodel we have to pass it in parametersOf
                val nowPlayingViewModel = koinViewModel<NowPlayingViewModel> {
                    parametersOf(key)
                }
                val nowPlayingUiState by nowPlayingViewModel.nowPlayingUiState.collectAsStateWithLifecycle(
                    initialValue = NowPlayingUiState()
                )

                val context = LocalContext.current
                ObserveAsEvents(nowPlayingViewModel.nowPlayingEvents) { events ->
                    when (events) {
                        is NowPlayingEvents.Minimize -> {
                            isMinimized = true
                            backStack.removeLastOrNull()
                        }

                        is NowPlayingEvents.ShowToast -> {
                            Toast.makeText(
                                context,
                                events.message.asString(context = context),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                NowPlayingScreen(
                    modifier = Modifier.fillMaxSize(),
                    nowPlayingUiState = nowPlayingUiState,
                    nowPlayingActions = nowPlayingViewModel::onActions
                )
            }

            entry<NavigationScreens.Search> {
                val searchViewModel = koinViewModel<SearchViewModel>()
                val searchUiState by searchViewModel.searchUiState.collectAsStateWithLifecycle()

                ObserveAsEvents(searchViewModel.searchEvents) { events ->
                    when (events) {
                        SearchEvents.NavigateBack -> backStack.removeLastOrNull()
                        is SearchEvents.NavigateToNowPlaying -> {
                            backStack.add(
                                NavigationScreens.NowPlaying(
                                    nowPlayingData = events.nowPlayingData
                                )
                            )
                            backStack.remove(NavigationScreens.Search)
                        }
                    }
                }

                SearchScreen(
                    modifier = Modifier.fillMaxSize(),
                    searchUiState = searchUiState,
                    searchActions = searchViewModel::onActions
                )
            }

            entry<NavigationScreens.AddSong> { key ->
                //here to use the key in viewmodel we have to pass it in parametersOf
                val addSongsViewModel = koinViewModel<AddSongsViewModel> {
                    parametersOf(key)
                }
                val addSongsUiState by addSongsViewModel.state.collectAsStateWithLifecycle()

                ObserveAsEvents(addSongsViewModel.events) { events ->
                    when (events) {
                        AddSongsEvents.NavigateBack -> backStack.removeLastOrNull()

                    }
                }

                AddSongsScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = addSongsUiState,
                    onActions = addSongsViewModel::onActions
                )
            }
        }
    )
}