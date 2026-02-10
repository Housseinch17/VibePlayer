package com.example.vibeplayer.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.vibeplayer.feature.add_songs.AddSongsRoot
import com.example.vibeplayer.feature.edit_playlist_songs.EditPlaylistSongsRoot
import com.example.vibeplayer.feature.main.presentation.MainRoot
import com.example.vibeplayer.feature.now_playing.presentation.NowPlayingRoot
import com.example.vibeplayer.feature.permission.presentation.PermissionRoot
import com.example.vibeplayer.feature.playlist_playback.PlaylistPlaybackRoot
import com.example.vibeplayer.feature.scan_music.presentation.ScanMusicRoot
import com.example.vibeplayer.feature.search.presentation.SearchRoot


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
                PermissionRoot(
                    navigateMainPage = {
                        backStack.add(NavigationScreens.MainPage)
                        backStack.remove(NavigationScreens.Permission)
                    }
                )
            }

            entry<NavigationScreens.MainPage> {
                MainRoot(
                    navigateToScanMusic = {
                        backStack.add(NavigationScreens.ScanMusic)
                    },
                    navigateToNowPlaying = { nowPlayingData ->
                        backStack.add(
                            NavigationScreens.NowPlaying(nowPlayingData)
                        )
                    },
                    navigateToSearch = {
                        backStack.add(NavigationScreens.Search)
                    },
                    navigateToAddSongs = { playlistName, playlistId ->
                        backStack.add(
                            NavigationScreens.AddSong(
                                playlistName,
                                playlistId
                            )
                        )
                    },
                    navigateToPlaylist = { playlistName ->
                        backStack.add(
                            NavigationScreens.PlaylistPlayback(
                                playlistName = playlistName
                            )
                        )
                    },
                    navigateToEdit = { playlistName, playlistId ->
                        backStack.add(
                            NavigationScreens.EditPlaylistSongs(
                                playlistName = playlistName,
                                playlistId = playlistId
                            )
                        )
                    },
                    isMinimized = isMinimized
                )
            }

            entry<NavigationScreens.ScanMusic> {
                ScanMusicRoot(
                    navigateBack = {
                        //backstack.removeLastOrNull() similar to navigateUp()
                        backStack.removeLastOrNull()
                    }
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
                NowPlayingRoot(
                    key = key,
                    updateIsMinimized = { updateIsMinimized ->
                        isMinimized = updateIsMinimized
                    },
                    navigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<NavigationScreens.Search> {
                SearchRoot(
                    navigateBack = { backStack.removeLastOrNull() },
                    navigateToNowPlaying = { nowPlayingData ->
                        backStack.add(
                            NavigationScreens.NowPlaying(
                                nowPlayingData = nowPlayingData
                            )
                        )
                        backStack.remove(NavigationScreens.Search)
                    }
                )
            }

            entry<NavigationScreens.AddSong> { key ->
                //note we placed it directly in composable if not in composable we use it like that
//                //here to use the key in viewmodel we have to pass it in parametersOf
//                val addSongsViewModel = koinViewModel<AddSongsViewModel> {
//                    parametersOf(key)
//                }

                AddSongsRoot(
                    key = key,
                    navigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<NavigationScreens.PlaylistPlayback> { key ->
                PlaylistPlaybackRoot(
                    key = key,
                    navigateBack = {
                        backStack.removeLastOrNull()
                    },
                    navigateToAddSongs = { playlistName, playlistId ->
                        backStack.add(
                            NavigationScreens.AddSong(
                                playlistName = playlistName,
                                playlistId = playlistId
                            )
                        )
                    },
                    navigateToNowPlaying = { nowPlayingData ->
                        backStack.add(
                            NavigationScreens.NowPlaying(nowPlayingData = nowPlayingData)
                        )
                    }
                )
            }

            entry<NavigationScreens.EditPlaylistSongs> { key ->
                EditPlaylistSongsRoot(
                    key = key,
                    navigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}