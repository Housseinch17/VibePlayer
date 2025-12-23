package com.example.vibeplayer.feature.main.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerAsyncImage
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerOutlinedButtonWithIcon
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPrimaryButton
import com.example.vibeplayer.core.presentation.designsystem.components.rotationIfScanning
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerImages
import com.example.vibeplayer.core.presentation.designsystem.theme.accent
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceBG
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import com.example.vibeplayer.core.util.toMinutesSeconds
import kotlinx.coroutines.launch

@Composable
fun MainPageScreen(
    modifier: Modifier = Modifier,
    mainPageUiState: MainPageUiState,
    onActions: (MainPageActions) -> Unit,
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (mainPageUiState.songState) {
                SongState.Empty -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        scanAgain = {
                            onActions(MainPageActions.ScanAgain)
                        })
                }

                SongState.Scanning -> {
                    ScanningState(modifier = Modifier.fillMaxSize())
                }

                is SongState.TrackList -> {
                    TrackListState(
                        modifier = Modifier,
                        state = lazyListState,
                        songList = mainPageUiState.songState.songList,
                        totalSongs = mainPageUiState.totalSong,
                        onSongItemClick = { id ->
                            onActions(
                                MainPageActions.NavigateToNowPlaying(
                                    nowPlayingData = NowPlayingData.Id(
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
                    )
                }
            }
        }
    }
}


@Composable
fun TrackListState(
    modifier: Modifier = Modifier,
    state: LazyListState,
    onSongItemClick: (Int) -> Unit,
    songList: List<Song>,
    totalSongs: Int,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                onClick = onShuffleClick
            )

            VibePlayerOutlinedButtonWithIcon(
                modifier = Modifier.weight(1f),
                buttonContentIconDescription = stringResource(R.string.play),
                buttonContentIconImageVector = VibePlayerIcons.Play,
                buttonContentText = stringResource(R.string.play),
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

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = state,
        ) {
            itemsIndexed(
                items = songList,
            ) { index, song ->
                SongItem(
                    modifier = Modifier,
                    song = song,
                    onSongItemClick = {
                        onSongItemClick(index)
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.textSecondary
                )
            }
        }
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
