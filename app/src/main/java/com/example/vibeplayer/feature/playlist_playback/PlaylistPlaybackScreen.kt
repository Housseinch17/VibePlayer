package com.example.vibeplayer.feature.playlist_playback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerAsyncImage
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerOutlinedButtonWithIcon
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerShuffleAndPlay
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import com.example.vibeplayer.feature.main.presentation.SongItem

@Composable
fun PlaylistPlaybackScreen(
    modifier: Modifier = Modifier,
    state: PlaylistPlaybackUiState,
    onActions: (PlaylistPlaybackActions) -> Unit,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        topBar = {
            VibePlayerIconShape(
                modifier = Modifier,
                imageVector = VibePlayerIcons.ArrowLeft,
                iconDescription = stringResource(R.string.back),
                onClick = {
                    onActions(PlaylistPlaybackActions.NavigateBack)
                },
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VibePlayerAsyncImage(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                imageUrl = state.playlist.embeddedUri,
                contentDescription = stringResource(R.string.playlist_image),
                errorDrawable = R.drawable.playlist_img,
            )
            Modifier.height(20.dp)
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = state.playlist.playlistName,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.textPrimary
                )
            )
            Spacer(modifier = Modifier.height(30.dp))
            PlaylistPlaybackState(
                playlistState = state.playlistState,
                addSongs = {
                    onActions(PlaylistPlaybackActions.AddSongs)
                },
                songs = state.songs,
                onShuffleClick = {
                    onActions(PlaylistPlaybackActions.NavigateAndShuffle)
                },
                onPlayClick = {
                    onActions(PlaylistPlaybackActions.NavigateAndPlay)
                },
                total = state.totalSongs,
                onAddClick = {
                    onActions(PlaylistPlaybackActions.AddSongs)
                },
            )
        }
    }
}

@Composable
fun PlaylistPlaybackState(
    modifier: Modifier = Modifier,
    playlistState: PlaylistState,
    addSongs: () -> Unit,
    songs: List<Song>,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    total: Int,
    onAddClick: () -> Unit,
) {
    when (playlistState) {
        PlaylistState.EMPTY -> {
            PlaylistPlaybackEmptyState(
                modifier = modifier,
                addSongs = addSongs
            )
        }

        PlaylistState.WITH_SONGS -> {
            PlaylistPlaybackWithSongsState(
                modifier = modifier,
                songs = songs,
                onShuffleClick = onShuffleClick,
                onPlayClick = onPlayClick,
                total = total,
                onAddClick = onAddClick
            )
        }
    }
}

@Composable
fun PlaylistPlaybackWithSongsState(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    total: Int,
    onAddClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        VibePlayerShuffleAndPlay(
            modifier = Modifier.fillMaxWidth(),
            onShuffleClick = onShuffleClick,
            onPlayClick = onPlayClick,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.total_songs, total),
                style = MaterialTheme.typography.bodyLargeMedium
            )
            VibePlayerIconShape(
                imageVector = VibePlayerIcons.Add,
                iconDescription = stringResource(R.string.add),
                onClick = onAddClick,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = rememberLazyListState()
        ) {
            items(songs, key = { it.id }) { song ->
                SongItem(
                    enabled = false,
                    song = song
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
fun PlaylistPlaybackEmptyState(
    modifier: Modifier = Modifier,
    addSongs: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_songs_found),
            style = MaterialTheme.typography.bodyLargeRegular
        )
        Spacer(modifier = Modifier.height(8.dp))
        VibePlayerOutlinedButtonWithIcon(
            onClick = addSongs,
            buttonContentIconImageVector = VibePlayerIcons.Add,
            buttonContentIconDescription = stringResource(R.string.add_songs),
            buttonContentText = stringResource(R.string.add_songs),
            borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceOutline),
        )

    }
}