package com.example.vibeplayer.feature.now_playing.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceBG
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary

@Composable
fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    nowPlayingUiState: NowPlayingUiState,
    nowPlayingActions: (NowPlayingActions) -> Unit,
) {
    Scaffold(
        modifier = modifier
            .padding(horizontal = 16.dp),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VibePlayerIconShape(
                    modifier = Modifier,
                    imageVector = VibePlayerIcons.ArrowLeft,
                    iconDescription = stringResource(R.string.back),
                    onClick = {
                        nowPlayingActions(NowPlayingActions.NavigateBack)
                    },
                )
            }
        },
        bottomBar = {
            NowPlayingBottomBar(
                modifier = Modifier.fillMaxWidth(),
                isPlaying = nowPlayingUiState.isPlaying,
                progressIndicator = nowPlayingUiState.progressIndicator,
                play = {
                    nowPlayingActions(NowPlayingActions.Play)
                },
                pause = {
                    nowPlayingActions(NowPlayingActions.Pause)
                },
                playNext = {
                    nowPlayingActions(NowPlayingActions.PlayNextSong)
                },
                playPrevious = {
                    nowPlayingActions(NowPlayingActions.PlayPreviousSong)
                }
            )
        }
    ) { innerPadding ->
        NowPlayingContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            song = nowPlayingUiState.song
        )
    }
}

@Composable
fun NowPlayingContent(
    modifier: Modifier = Modifier,
    song: Song,
) {
    val verticalScroll = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(verticalScroll)
            .padding(horizontal = 30.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VibePlayerAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.small),
            imageUrl = song.embeddedArt,
            contentDescription = stringResource(R.string.song_image),
            errorDrawable = R.drawable.song_default,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = song.title,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = song.artist,
            style = MaterialTheme.typography.bodyMediumRegular
        )
    }
}

@Composable
fun NowPlayingBottomBar(
    modifier: Modifier = Modifier,
    progressIndicator: Float,
    isPlaying: Boolean,
    playPrevious: () -> Unit,
    playNext: () -> Unit,
    play: () -> Unit,
    pause: () -> Unit,
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            progress = { progressIndicator },
            color = MaterialTheme.colorScheme.textPrimary,
            trackColor = MaterialTheme.colorScheme.surfaceOutline,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VibePlayerIconShape(
                modifier = Modifier.size(44.dp),
                imageVector = VibePlayerIcons.SkipPrevious,
                iconDescription = stringResource(R.string.play_previous),
                onClick = playPrevious,
            )

            VibePlayerIconShape(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(60.dp),
                imageVector = if (isPlaying) VibePlayerIcons.Pause else VibePlayerIcons.Play,
                containerColor = MaterialTheme.colorScheme.textPrimary,
                iconDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(
                    R.string.play
                ),
                iconModifier = Modifier.size(20.dp),
                onClick = {
                    if (isPlaying) {
                        pause()
                    } else {
                        play()
                    }
                },
                tintColor = MaterialTheme.colorScheme.surfaceBG,
            )

            VibePlayerIconShape(
                modifier = Modifier.size(44.dp),
                imageVector = VibePlayerIcons.SkipNext,
                iconDescription = stringResource(R.string.play_next),
                onClick = playNext,
            )
        }
    }
}