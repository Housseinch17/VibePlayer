@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.vibeplayer.feature.now_playing.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerAsyncImage
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPlayButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPlayNextButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPlayPreviousButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerRepeatButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerShuffleButton
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.bodySmallRegular
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
                    imageVector = VibePlayerIcons.ChevronDown,
                    iconDescription = stringResource(R.string.minimize),
                    onClick = {
                        nowPlayingActions(NowPlayingActions.Minimize)
                    },
                )
            }
        },
        bottomBar = {
            NowPlayingBottomBar(
                modifier = Modifier.fillMaxWidth(),
                isPlaying = nowPlayingUiState.mediaPlayerState.isPlaying,
                progressIndicator = nowPlayingUiState.progressIndicator,
                progressIndicatorForLinear = nowPlayingUiState.progressIndicatorForLinearProgress,
                valueRanged = nowPlayingUiState.valueRanged,
                sliderThumbText = nowPlayingUiState.sliderThumbText,
                onIndicatorChange = { position ->
                    nowPlayingActions(NowPlayingActions.SeekTo(position = position.toLong()))
                },
                play = {
                    nowPlayingActions(NowPlayingActions.Play)
                },
                pause = {
                    nowPlayingActions(NowPlayingActions.Pause)
                },
                playNext = {
                    nowPlayingActions(NowPlayingActions.PlayNext)
                },
                playPrevious = {
                    nowPlayingActions(NowPlayingActions.PlayPrevious)
                },
                shuffle = {
                    nowPlayingActions(NowPlayingActions.Shuffle)
                },
                isShuffled = nowPlayingUiState.mediaPlayerState.isShuffled,
                repeatMode = nowPlayingUiState.mediaPlayerState.repeatMode,
                clickRepeatMode = {
                    nowPlayingActions(NowPlayingActions.RepeatMode)
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
    progressIndicatorForLinear: Float,
    onIndicatorChange: (Float) -> Unit,
    valueRanged: ClosedFloatingPointRange<Float>,
    sliderThumbText: String,
    isPlaying: Boolean,
    playPrevious: () -> Unit,
    playNext: () -> Unit,
    play: () -> Unit,
    pause: () -> Unit,
    shuffle: () -> Unit,
    isShuffled: Boolean,
    clickRepeatMode: () -> Unit,
    repeatMode: Int,
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val maxWidth = maxWidth
            Slider(
                modifier = Modifier.matchParentSize(),
                value = progressIndicator,
                onValueChange = onIndicatorChange,
                valueRange = valueRanged,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.textPrimary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceOutline,
                    inactiveTickColor = Color.Red,
                    activeTickColor = Color.Blue,
                    thumbColor = MaterialTheme.colorScheme.textPrimary,
                ),
                thumb = {
                    Text(
                        modifier = Modifier
                            .height(16.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(
                                color = MaterialTheme.colorScheme.textPrimary,
                                shape = MaterialTheme.shapes.large
                            )
                            .padding(horizontal = 4.dp),
                        text = sliderThumbText,
                        style = MaterialTheme.typography.bodySmallRegular
                    )
                },
                track = {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .requiredWidth(maxWidth)
                            .height(6.dp),
                        progress = { progressIndicatorForLinear },
                        color = MaterialTheme.colorScheme.textPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceOutline,
                    )
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VibePlayerShuffleButton(
                modifier = Modifier,
                shuffle = shuffle,
                isShuffled = isShuffled
            )
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                VibePlayerPlayPreviousButton(
                    modifier = Modifier,
                    playPrevious = playPrevious
                )

                VibePlayerPlayButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(60.dp),
                    isPlaying = isPlaying,
                    pause = pause,
                    play = play
                )

                VibePlayerPlayNextButton(
                    modifier = Modifier,
                    playNext = playNext
                )

                Spacer(modifier = Modifier.weight(1f))
            }
            VibePlayerRepeatButton(
                modifier = Modifier,
                repeatMode = repeatMode,
                repeatModeClick = clickRepeatMode
            )
        }
    }
}