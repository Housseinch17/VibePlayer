@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vibeplayer.feature.main.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vibeplayer.R
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.core.data.Constants.FAVOURITE
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerAsyncImage
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerBottomSheet
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerMiniBar
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerOutlinedButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerOutlinedButtonWithIcon
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPlaylistItem
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPrimaryButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerShuffleAndPlay
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerSnackbar
import com.example.vibeplayer.core.presentation.designsystem.components.rotationIfScanning
import com.example.vibeplayer.core.presentation.designsystem.theme.TextPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerImages
import com.example.vibeplayer.core.presentation.designsystem.theme.accent
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceBG
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import com.example.vibeplayer.core.presentation.ui.ObserveAsEvents
import com.example.vibeplayer.core.util.copyImageToInternalStorage
import com.example.vibeplayer.core.util.toMinutesSeconds
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun MainRoot(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel<MainViewModel>(),
    navigateToScanMusic: () -> Unit,
    navigateToNowPlaying: (NowPlayingData) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAddSongs: (playlistName: String, playlistId: Int) -> Unit,
    navigateToPlaylist: (playlistName: String) -> Unit,
    navigateToEdit: (playlistName: String, playlistId: Int) -> Unit,
    isMinimized: Boolean,
) {
    val mainUiState by mainViewModel.mainPageUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    ObserveAsEvents(mainViewModel.mainPageEvents) { events ->
        when (events) {
            MainPageEvents.NavigateToScanMusic -> {
                navigateToScanMusic()
            }

            is MainPageEvents.NavigateToNowPlaying -> {
                navigateToNowPlaying(events.nowPlayingData)
            }

            MainPageEvents.NavigateToSearch -> {
                navigateToSearch()
            }

            is MainPageEvents.NavigateToAddSongs -> {
                navigateToAddSongs(
                    events.playlistName,
                    events.playlistId
                )
            }

            is MainPageEvents.ShowToast -> Toast.makeText(
                context,
                events.message.asString(context = context),
                Toast.LENGTH_LONG
            ).show()

            is MainPageEvents.NavigateToPlaylist -> {
                navigateToPlaylist(events.playlistName)
            }

            is MainPageEvents.NavigateToEdit -> {
                navigateToEdit(
                    events.playlistName,
                    events.playlistId
                )
            }
        }
    }

    MainPageScreen(
        modifier = modifier.fillMaxSize(),
        mainPageUiState = mainUiState,
        onActions = mainViewModel::onActions,
        isMinimized = isMinimized
    )
}

@Composable
fun MainPageScreen(
    modifier: Modifier = Modifier,
    mainPageUiState: MainPageUiState,
    onActions: (MainPageActions) -> Unit,
    isMinimized: Boolean,
) {
    val context = LocalContext.current

    //google docs:
    //https://developer.android.com/training/data-storage/shared/photo-picker
    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            uri?.let {
                val imageStorageUri = copyImageToInternalStorage(
                    context = context,
                    uri = it,
                    fileId = mainPageUiState.currentPlaylist.id
                )
                onActions(MainPageActions.OnChangeCover(imageUri = imageStorageUri))
            } ?: Toast.makeText(context, R.string.change_cover_failed, Toast.LENGTH_LONG).show()
        }

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val showFAB by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 10
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mainPageUiState.snackbarMessage) {
        if (mainPageUiState.snackbarMessage != null) {
            snackBarHostState.showSnackbar(
                message = mainPageUiState.snackbarMessage.asString(context = context),
                withDismissAction = false
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceBG,
        topBar = {
            MainPageTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 10.dp),
                isEnabled = mainPageUiState.songState !is SongState.Scanning,
                onScanClick = {
                    onActions(MainPageActions.NavigateToScanMusic)
                },
                onSearchClick = {
                    onActions(MainPageActions.NavigateToSearch)
                },
                isSearchVisible = mainPageUiState.songState is SongState.TrackList
            )
        },
        floatingActionButton = {
            MainPageFAB(
                modifier = Modifier,
                isVisible = showFAB,
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        bottomBar = {
            if (isMinimized) {
                onActions(MainPageActions.SetCurrentSong)
                VibePlayerMiniBar(
                    modifier = Modifier,
                    song = mainPageUiState.currentSong,
                    isPlaying = mainPageUiState.mediaPlayerState.isPlaying,
                    play = {
                        onActions(MainPageActions.Play)
                    },
                    pause = {
                        onActions(MainPageActions.Pause)
                    },
                    playNext = {
                        onActions(MainPageActions.PlayNext)
                    },
                    playPrevious = {
                        onActions(MainPageActions.PlayPrevious)
                    },
                    progressIndicator = mainPageUiState.progressIndicatorForLinearProgress,
                    onClick = { id ->
                        onActions(
                            MainPageActions.NavigateToNowPlaying(
                                nowPlayingData = NowPlayingData.PlayBySongId(
                                    id = id
                                )
                            )
                        )
                    },
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    VibePlayerSnackbar(
                        snackbarData = data
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (mainPageUiState.songState) {
                SongState.Empty -> {
                    EmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        scanAgain = {
                            onActions(MainPageActions.ScanAgain)
                        })
                }

                SongState.Scanning -> {
                    ScanningState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                }

                is SongState.TrackList -> {
                    TrackListState(
                        modifier = Modifier,
                        selectedMainTabs = mainPageUiState.selectedMainTabs,
                        state = lazyListState,
                        songList = mainPageUiState.songState.songList,
                        totalSongs = mainPageUiState.totalSong,
                        totalPlaylist = mainPageUiState.myPlayList.size + 1,
                        onSongItemClick = { id ->
                            onActions(
                                MainPageActions.NavigateToNowPlaying(
                                    nowPlayingData = NowPlayingData.PlayBySongId(
                                        id = id
                                    )
                                )
                            )
                        },
                        onShuffleClick = {
                            onActions(MainPageActions.NavigateAndShuffle)
                        },
                        onPlayClick = {
                            onActions(MainPageActions.NavigateAndPlay)
                        },
                        onMainTabsSelect = { mainTabs ->
                            onActions(MainPageActions.UpdateMainTabs(mainTabs = mainTabs))
                        },
                        favouritePlayList = mainPageUiState.favouritePlayList,
                        onMenuDotsClick = { playlistModel ->
                            onActions(MainPageActions.OnMenuDots(playListModel = playlistModel))
                        },
                        myPlatListList = mainPageUiState.myPlayList,
                        onCreatePlayList = {
                            onActions(MainPageActions.ShowCreatePlaylistBottomSheet)
                        },
                        isBottomSheetVisible = mainPageUiState.isShowCreatePlaylistBottomSheetVisible,
                        isCreateEnabled = mainPageUiState.isCreateEnabled,
                        onCreateClick = {
                            onActions(MainPageActions.NavigateToAddSongs)
                        },
                        onCancelClick = {
                            onActions(MainPageActions.HideBottomSheet)
                        },
                        playListTextFieldValue = mainPageUiState.playListTextField,
                        onPlaylistValueChange = { newValue ->
                            onActions(MainPageActions.UpdatePlaylistTextField(value = newValue))
                        },
                        currentPlayListModel = mainPageUiState.currentPlaylist,
                        onDismiss = {
                            onActions(MainPageActions.OnMenuDotsDismiss)
                        },
                        onPlayBottomSheetClick = { playlistName ->
                            onActions(MainPageActions.OnPlaylist(playlistName = playlistName))
                        },
                        onEditClick = { playlistId, playlistName ->
                            onActions(
                                MainPageActions.NavigateToEdit(
                                    playlistId = playlistId,
                                    playlistName = playlistName
                                )
                            )
                        },
                        onRenameClick = {
                            onActions(MainPageActions.ShowRenameBottomSheet)
                        },
                        onChangeCoverClick = {
                            //mimeType means the type of the selected items from gallery
                            //should be image only
//                            val mimeType = "image/*"
//                            galleryLauncher.launch(mimeType)
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        onDeleteClick = {
                            onActions(MainPageActions.OnDelete)
                        },
                        showCurrentPlaylistBottomSheet = mainPageUiState.showCurrentPlaylistBottomSheet,
                        showRenameBottomSheet = mainPageUiState.showRenameBottomSheet,
                        cancelRenameBottomSheet = {
                            onActions(MainPageActions.HideRenameBottomSheet)
                        },
                        renameTextField = mainPageUiState.renameTextField,
                        isRenameEnabled = mainPageUiState.isRenameEnabled,
                        onRenamePlaylist = {
                            onActions(MainPageActions.OnRename)
                        },
                        updateRenameTextField = { renameNewValue ->
                            onActions(MainPageActions.UpdateRenameTextField(newValue = renameNewValue))
                        },
                        showDeleteBottomSheet = mainPageUiState.showDeleteBottomSheet,
                        onCancelDeleteBottomSheet = {
                            onActions(MainPageActions.HideDeleteBottomSheet)
                        },
                        onDeletePlaylistBottomSheet = {
                            onActions(MainPageActions.OnDeletePlaylistBottomSheet)
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun TrackListState(
    modifier: Modifier = Modifier,
    selectedMainTabs: MainTabs,
    state: LazyListState,
    onSongItemClick: (Int) -> Unit,
    songList: List<Song>,
    totalSongs: Int,
    totalPlaylist: Int,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    onMainTabsSelect: (MainTabs) -> Unit,
    favouritePlayList: PlayListModel,
    onMenuDotsClick: (PlayListModel) -> Unit,
    myPlatListList: List<PlayListModel>,
    onCreatePlayList: () -> Unit,
    isBottomSheetVisible: Boolean,
    isCreateEnabled: Boolean,
    onCreateClick: () -> Unit,
    onCancelClick: () -> Unit,
    playListTextFieldValue: String,
    onPlaylistValueChange: (String) -> Unit,
    currentPlayListModel: PlayListModel,
    onDismiss: () -> Unit,
    onPlayBottomSheetClick: (String) -> Unit,
    onEditClick: (playlistId: Int, playlistName: String) -> Unit,
    onRenameClick: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onDeleteClick: () -> Unit,
    showCurrentPlaylistBottomSheet: Boolean,
    showRenameBottomSheet: Boolean,
    cancelRenameBottomSheet: () -> Unit,
    updateRenameTextField: (String) -> Unit,
    renameTextField: String,
    isRenameEnabled: Boolean,
    onRenamePlaylist: () -> Unit,
    showDeleteBottomSheet: Boolean,
    onCancelDeleteBottomSheet: () -> Unit,
    onDeletePlaylistBottomSheet: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                color = MaterialTheme.colorScheme.surfaceOutline
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MainTabsItem(
                    modifier = Modifier,
                    mainTabs = MainTabs.SONGS,
                    selectedMainTabs = selectedMainTabs,
                    onMainTabsSelect = {
                        onMainTabsSelect(MainTabs.SONGS)
                    },
                )
                MainTabsItem(
                    modifier = Modifier,
                    mainTabs = MainTabs.PLAYLIST,
                    selectedMainTabs = selectedMainTabs,
                    onMainTabsSelect = {
                        onMainTabsSelect(MainTabs.PLAYLIST)
                    },
                )
            }
        }

        when (selectedMainTabs) {
            MainTabs.SONGS -> {
                SongsContent(
                    state = state,
                    onSongItemClick = onSongItemClick,
                    songList = songList,
                    totalSongs = totalSongs,
                    onShuffleClick = onShuffleClick,
                    onPlayClick = onPlayClick
                )
            }

            else -> {
                Column {
                    PlaylistContent(
                        totalPlaylist = totalPlaylist,
                        onAddClick = onCreatePlayList,
                        favouritePlayList = favouritePlayList,
                        onMenuDotsClick = onMenuDotsClick,
                        myPlatListList = myPlatListList,
                        onCreatePlayList = onCreatePlayList,
                        currentPlayListModel = currentPlayListModel,
                        onDismiss = onDismiss,
                        onPlayBottomSheetClick = onPlayBottomSheetClick,
                        onEditClick = onEditClick,
                        onRenameClick = onRenameClick,
                        onChangeCoverClick = onChangeCoverClick,
                        onDeleteClick = onDeleteClick,
                        showCurrentPlaylistBottomSheet = showCurrentPlaylistBottomSheet
                    )
                    if (isBottomSheetVisible) {
                        VibePlayerBottomSheet(
                            modifier = Modifier,
                            value = playListTextFieldValue,
                            onValueChange = { newValue ->
                                onPlaylistValueChange(newValue)
                            },
                            isButtonEnabled = isCreateEnabled,
                            onCancelClick = onCancelClick,
                            onButtonClick = onCreateClick
                        )
                    }
                    if (showRenameBottomSheet) {
                        VibePlayerBottomSheet(
                            modifier = Modifier,
                            text = stringResource(R.string.rename_playlist),
                            buttonText = stringResource(R.string.rename),
                            value = renameTextField,
                            onValueChange = { newValue ->
                                updateRenameTextField(newValue)
                            },
                            isButtonEnabled = isRenameEnabled,
                            onCancelClick = cancelRenameBottomSheet,
                            onButtonClick = onRenamePlaylist
                        )
                    }
                    if (showDeleteBottomSheet) {
                        MainScreenDeleteBottomSheet(
                            onCancel = onCancelDeleteBottomSheet,
                            onDelete = onDeletePlaylistBottomSheet
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenDeleteBottomSheet(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = onCancel,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceBG
                )
                .blur(12.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = MaterialTheme.colorScheme.surfaceBG.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.surfaceBG.copy(alpha = 0.3f)
                )
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.delete_playlist),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.textPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.delete_description),
                style = MaterialTheme.typography.bodyMediumRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                VibePlayerOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.cancel),
                    onClick = onCancel
                )
                Spacer(modifier = Modifier.width(12.dp))
                VibePlayerPrimaryButton(
                    modifier = Modifier.weight(1f),
                    onclick = onDelete,
                    text = stringResource(R.string.delete),
                    isButtonDestructive = true
                )
            }
        }
    }
}

@Composable
fun SongsContent(
    modifier: Modifier = Modifier,
    state: LazyListState,
    onSongItemClick: (Int) -> Unit,
    songList: List<Song>,
    totalSongs: Int,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        state = state,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            VibePlayerShuffleAndPlay(
                onShuffleClick = onShuffleClick,
                onPlayClick = onPlayClick
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = stringResource(R.string.total_songs, totalSongs),
                style = MaterialTheme.typography.bodyLargeMedium.copy(
                    textAlign = TextAlign.Start
                )
            )
        }

        items(
            items = songList,
            key = { song ->
                song.id
            },
        ) { song ->
            SongItem(
                modifier = Modifier,
                song = song,
                onSongItemClick = {
                    onSongItemClick(song.id)
                }
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceOutline
            )
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun PlaylistContent(
    modifier: Modifier = Modifier,
    totalPlaylist: Int,
    onAddClick: () -> Unit,
    favouritePlayList: PlayListModel,
    myPlatListList: List<PlayListModel>,
    onMenuDotsClick: (PlayListModel) -> Unit,
    onCreatePlayList: () -> Unit,
    currentPlayListModel: PlayListModel,
    onDismiss: () -> Unit,
    onPlayBottomSheetClick: (String) -> Unit,
    onEditClick: (playlistId: Int, playlistName: String) -> Unit,
    onRenameClick: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onDeleteClick: () -> Unit,
    showCurrentPlaylistBottomSheet: Boolean,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                //note that plurals treat "zero" as not exist or something
                //here the playList is never 0 either 1 or more so we can remove the condition
                //if totalPlaylist == 0
                text = context.resources.getQuantityString(
                    R.plurals.playlist_size,
                    totalPlaylist,
                    totalPlaylist
                ),
                style = MaterialTheme.typography.bodyLargeMedium.copy(
                    color = MaterialTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Start
                )
            )
            VibePlayerIconShape(
                modifier = Modifier,
                imageVector = VibePlayerIcons.Add,
                iconDescription = stringResource(R.string.add),
                onClick = onAddClick
            )
        }
        VibePlayerPlaylistItem(
            playListModel = favouritePlayList,
            onMenuDotsClick = { onMenuDotsClick(favouritePlayList) },
            isFavourite = true
        )
        Text(
            modifier = Modifier,
            text = stringResource(R.string.my_playlist, myPlatListList.size),
            style = MaterialTheme.typography.bodyLargeMedium.copy(
                color = MaterialTheme.colorScheme.textSecondary,
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (myPlatListList.isNotEmpty()) {
            MyPlaylists(
                myPlatListList = myPlatListList,
                onMenuDotsClick = { playListModel ->
                    onMenuDotsClick(playListModel)
                },
                currentPlayListModel = currentPlayListModel,
                onDismiss = onDismiss,
                onPlayBottomSheetClick = onPlayBottomSheetClick,
                onEditClick = onEditClick,
                onRenameClick = onRenameClick,
                onChangeCoverClick = onChangeCoverClick,
                onDeleteClick = onDeleteClick,
                showCurrentPlaylistBottomSheet = showCurrentPlaylistBottomSheet,
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            VibePlayerOutlinedButtonWithIcon(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreatePlayList,
                buttonContentIconImageVector = VibePlayerIcons.Add,
                buttonContentIconDescription = stringResource(R.string.add),
                buttonContentText = stringResource(R.string.create_playlist),
                borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceOutline)
            )
        }
    }
}


@Composable
fun MainScreenBottomSheet(
    modifier: Modifier = Modifier,
    playListModel: PlayListModel,
    onDismiss: () -> Unit,
    onPlayBottomSheetClick: (String) -> Unit,
    onEditClick: (playlistId: Int, playlistName: String) -> Unit,
    onRenameClick: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceBG
                )
                .blur(12.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = MaterialTheme.colorScheme.surfaceBG.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.surfaceBG.copy(alpha = 0.3f)
                )
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            VibePlayerPlaylistItem(
                playListModel = playListModel,
                showDots = false,
                enabled = false,
                isFavourite = playListModel.name == FAVOURITE
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.textSecondary
            )
            MainScreenClickableIconAndText(
                imageVector = VibePlayerIcons.Play,
                iconDescription = stringResource(R.string.play),
                text = stringResource(R.string.play),
                onClick = {
                    onPlayBottomSheetClick(playListModel.name)
                }
            )
            MainScreenClickableIconAndText(
                imageVector = VibePlayerIcons.Edit,
                iconDescription = stringResource(R.string.edit_songs),
                text = stringResource(R.string.edit_songs),
                onClick = {
                    onEditClick(playListModel.id, playListModel.name)
                }
            )
            if (playListModel.name != FAVOURITE) {
                Column {
                    MainScreenClickableIconAndText(
                        imageVector = VibePlayerIcons.Rename,
                        iconDescription = stringResource(R.string.rename),
                        text = stringResource(R.string.rename),
                        onClick = onRenameClick
                    )

                    MainScreenClickableIconAndText(
                        imageVector = VibePlayerIcons.ChangeCover,
                        iconDescription = stringResource(R.string.change_cover),
                        text = stringResource(R.string.change_cover),
                        onClick = onChangeCoverClick
                    )

                    MainScreenClickableIconAndText(
                        imageVector = VibePlayerIcons.Delete,
                        iconDescription = stringResource(R.string.delete),
                        text = stringResource(R.string.delete),
                        onClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreenClickableIconAndText(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconDescription: String,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        VibePlayerIconShape(
            imageVector = imageVector,
            iconDescription = iconDescription,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLargeMedium.copy(
                color = MaterialTheme.colorScheme.textPrimary,
                textAlign = TextAlign.Start
            )
        )
    }
}

@Composable
fun MyPlaylists(
    modifier: Modifier = Modifier,
    myPlatListList: List<PlayListModel>,
    onMenuDotsClick: (PlayListModel) -> Unit,
    currentPlayListModel: PlayListModel,
    onDismiss: () -> Unit,
    onPlayBottomSheetClick: (String) -> Unit,
    onEditClick: (playlistId: Int, playlistName: String) -> Unit,
    onRenameClick: () -> Unit,
    onChangeCoverClick: () -> Unit,
    onDeleteClick: () -> Unit,
    showCurrentPlaylistBottomSheet: Boolean,
) {
    Box(
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(myPlatListList) { playListModel ->
                VibePlayerPlaylistItem(
                    playListModel = playListModel,
                    onMenuDotsClick = {
                        onMenuDotsClick(playListModel)
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceOutline
                )
            }
        }
        if (showCurrentPlaylistBottomSheet) {
            MainScreenBottomSheet(
                playListModel = currentPlayListModel,
                onDismiss = onDismiss,
                onPlayBottomSheetClick = onPlayBottomSheetClick,
                onEditClick = onEditClick,
                onRenameClick = onRenameClick,
                onChangeCoverClick = onChangeCoverClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
fun MainTabsItem(
    modifier: Modifier = Modifier,
    mainTabs: MainTabs,
    selectedMainTabs: MainTabs,
    onMainTabsSelect: () -> Unit,
) {
    Column(
        modifier = modifier.width(intrinsicSize = IntrinsicSize.Max),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.clickable(onClick = onMainTabsSelect),
            text = stringResource(mainTabs.resourceId),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = if (mainTabs == selectedMainTabs) TextPrimary else Color.Transparent
        )
    }
}

@Composable
fun ScanningState(
    modifier: Modifier = Modifier,
) {
    val rotationEveryTwoSeconds = rotationIfScanning()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(140.dp)
                .rotate(rotationEveryTwoSeconds),
            painter = VibePlayerImages.ScanningRadarImage,
            contentDescription = stringResource(R.string.radar)
        )
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(R.string.scanning_your_device_for_music),
            style = MaterialTheme.typography.bodyMediumRegular.copy(
                color = MaterialTheme.colorScheme.textSecondary
            )
        )
    }
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    scanAgain: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(R.string.no_music_found),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(R.string.try_scanning_again),
            style = MaterialTheme.typography.bodyMediumRegular.copy(
                color = MaterialTheme.colorScheme.textSecondary,
            )
        )

        VibePlayerPrimaryButton(
            modifier = Modifier.padding(top = 20.dp),
            onclick = scanAgain,
            text = stringResource(R.string.scan_again)
        )
    }
}

@Composable
fun SongItem(
    modifier: Modifier = Modifier,
    onSongItemClick: () -> Unit = {},
    enabled: Boolean = true,
    song: Song
) {
    Row(
        modifier = modifier
            .padding(vertical = 12.dp)
            .clickable(onClick = onSongItemClick, enabled = enabled),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VibePlayerAsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.small),
            imageUrl = song.embeddedArt,
            errorDrawable = R.drawable.song_default,
            contentDescription = stringResource(R.string.audio_description)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
            )
            Text(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .horizontalScroll(rememberScrollState()),
                text = song.artist,
                style = MaterialTheme.typography.bodyMediumRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary
                ),
                maxLines = 1,
            )
        }

        Text(
            modifier = Modifier,
            text = song.duration.toMinutesSeconds(),
            style = MaterialTheme.typography.bodyMediumRegular
        )
    }
}

@Composable
fun MainPageTopBar(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onScanClick: () -> Unit,
    onSearchClick: () -> Unit,
    isSearchVisible: Boolean,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainPageTextWithLeadingIcon(
            modifier = Modifier.weight(1f),
            imageVector = VibePlayerIcons.Logo,
            iconDescription = stringResource(R.string.logo_icon),
            text = stringResource(R.string.app_name)
        )

        VibePlayerIconShape(
            modifier = Modifier,
            imageVector = VibePlayerIcons.Scan,
            iconDescription = stringResource(R.string.scan),
            isEnabled = isEnabled,
            onClick = onScanClick
        )

        if (isSearchVisible) {
            VibePlayerIconShape(
                modifier = Modifier.padding(start = 10.dp),
                imageVector = VibePlayerIcons.Search,
                iconDescription = stringResource(R.string.search),
                isEnabled = isEnabled,
                onClick = onSearchClick
            )
        }

    }
}

@Composable
fun MainPageFAB(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible
    ) {
        VibePlayerIconShape(
            modifier = modifier.size(56.dp),
            imageVector = VibePlayerIcons.ArrowUp,
            iconDescription = stringResource(R.string.floating_action_button),
            iconModifier = Modifier.size(26.dp),
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.buttonPrimary,
            tintColor = MaterialTheme.colorScheme.textPrimary
        )
    }
}

@Composable
fun MainPageTextWithLeadingIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconDescription: String,
    text: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier,
            imageVector = imageVector,
            contentDescription = iconDescription,
            tint = MaterialTheme.colorScheme.accent
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = text,
            style = MaterialTheme.typography.bodyLargeMedium.copy(
                color = MaterialTheme.colorScheme.accent
            )
        )
    }
}