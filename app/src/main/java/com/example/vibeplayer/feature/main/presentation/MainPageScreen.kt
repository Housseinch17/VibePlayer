package com.example.vibeplayer.feature.main.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerAsyncImage
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerBottomSheet
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerMiniBar
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerOutlinedButtonWithIcon
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPrimaryButton
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
import com.example.vibeplayer.core.util.toMinutesSeconds
import kotlinx.coroutines.launch

@Composable
fun MainPageScreen(
    modifier: Modifier = Modifier,
    mainPageUiState: MainPageUiState,
    onActions: (MainPageActions) -> Unit,
    isMinimized: Boolean,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val showFAB by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 10
        }
    }

    Scaffold(
        modifier = modifier,
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
                    onClick = { songId ->
                        onActions(
                            MainPageActions.NavigateToNowPlaying(
                                nowPlayingData = NowPlayingData.PlayBySongId(
                                    songId = songId
                                )
                            )
                        )
                    },
                )
            }
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
                        onSongItemClick = { songId ->
                            onActions(
                                MainPageActions.NavigateToNowPlaying(
                                    nowPlayingData = NowPlayingData.PlayBySongId(
                                        songId = songId
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
                        favoritePlayList = mainPageUiState.favoritePlayList,
                        onMenuDotsClick = {

                        },
                        myPlatListList = mainPageUiState.myPlayList,
                        onCreatePlayList = {
                            onActions(MainPageActions.ShowBottomSheet)
                        },
                        isBottomSheetVisible = mainPageUiState.isBottomSheetVisible,
                        isCreateEnabled = mainPageUiState.isCreateEnabled,
                        onCreateClick = {
                            onActions(MainPageActions.OnCreatePlayListClick)
                        },
                        onCancelClick = {
                            onActions(MainPageActions.HideBottomSheet)
                        },
                        playListTextFieldValue = mainPageUiState.playListTextField,
                        onPlaylistValueChange = { newValue ->
                            onActions(MainPageActions.UpdatePlaylistTextField(value = newValue))
                        },
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
    onSongItemClick: (Long) -> Unit,
    songList: List<Song>,
    totalSongs: Int,
    totalPlaylist: Int,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    onMainTabsSelect: (MainTabs) -> Unit,
    favoritePlayList: PlayListModel,
    onMenuDotsClick: (PlayListModel) -> Unit,
    myPlatListList: List<PlayListModel>,
    onCreatePlayList: () -> Unit,
    isBottomSheetVisible: Boolean,
    isCreateEnabled: Boolean,
    onCreateClick: () -> Unit,
    onCancelClick: () -> Unit,
    playListTextFieldValue: String,
    onPlaylistValueChange: (String) -> Unit,
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
                        favoritePlayList = favoritePlayList,
                        onMenuDotsClick = onMenuDotsClick,
                        myPlatListList = myPlatListList,
                        onCreatePlayList = onCreatePlayList,
                    )
                    if (isBottomSheetVisible) {
                        VibePlayerBottomSheet(
                            modifier = Modifier,
                            value = playListTextFieldValue,
                            onValueChange = { newValue ->
                                onPlaylistValueChange(newValue)
                            },
                            isCreateEnabled = isCreateEnabled,
                            onCancelClick = onCancelClick,
                            onCreateClick = onCreateClick
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SongsContent(
    modifier: Modifier = Modifier,
    state: LazyListState,
    onSongItemClick: (Long) -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VibePlayerOutlinedButtonWithIcon(
                    modifier = Modifier.weight(1f),
                    buttonContentIconDescription = stringResource(R.string.shuffle),
                    buttonContentIconImageVector = VibePlayerIcons.Shuffle,
                    buttonContentText = stringResource(R.string.shuffle),
                    borderStroke = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.surfaceOutline
                    ),
                    onClick = onShuffleClick
                )

                VibePlayerOutlinedButtonWithIcon(
                    modifier = Modifier.weight(1f),
                    buttonContentIconDescription = stringResource(R.string.play),
                    buttonContentIconImageVector = VibePlayerIcons.Play,
                    buttonContentText = stringResource(R.string.play),
                    borderStroke = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.surfaceOutline
                    ),
                    onClick = onPlayClick,
                )
            }
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
                    onSongItemClick(song.songId)
                }
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceOutline
            )
        }
    }
}

@Composable
fun PlaylistContent(
    modifier: Modifier = Modifier,
    totalPlaylist: Int,
    onAddClick: () -> Unit,
    favoritePlayList: PlayListModel,
    myPlatListList: List<PlayListModel>,
    onMenuDotsClick: (PlayListModel) -> Unit,
    onCreatePlayList: () -> Unit,
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
        PlaylistItem(
            playListModel = favoritePlayList,
            onMenuDotsClick = { onMenuDotsClick(favoritePlayList) }
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
                }
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
fun MyPlaylists(
    modifier: Modifier = Modifier,
    myPlatListList: List<PlayListModel>,
    onMenuDotsClick: (PlayListModel) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(myPlatListList) { playListModel ->
            PlaylistItem(
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
}

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    playListModel: PlayListModel,
    onMenuDotsClick: (PlayListModel) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VibePlayerAsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            imageUrl = playListModel.embeddedArt,
            contentDescription = stringResource(R.string.playlist_image),
            errorDrawable = playListModel.errorDrawable,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                modifier = Modifier,
                text = playListModel.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.textPrimary,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                modifier = Modifier,
                text = if (playListModel.total == 0) "0 ${stringResource(R.string.song)}"
                else context.resources.getQuantityString(
                    R.plurals.song_size,
                    playListModel.total,
                    playListModel.total
                ),
                style = MaterialTheme.typography.bodyMediumRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Start
                )
            )
        }
        Image(
            modifier = Modifier.clickable(onClick = { onMenuDotsClick(playListModel) }),
            imageVector = VibePlayerIcons.MenuDots,
            contentDescription = stringResource(R.string.menu_dots),
        )
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
    onSongItemClick: () -> Unit,
    song: Song
) {
    Row(
        modifier = modifier
            .padding(vertical = 12.dp)
            .clickable(onClick = onSongItemClick),
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