package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceBG
import com.example.vibeplayer.core.presentation.designsystem.theme.textDisabled
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary

@Composable
fun VibePlayerPlayButton(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    pause: () -> Unit,
    play: () -> Unit,
) {
    VibePlayerIconShape(
        modifier = modifier,
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
}

@Composable
fun VibePlayerPlayNextButton(
    modifier: Modifier = Modifier,
    playNext: () -> Unit,
) {
    VibePlayerIconShape(
        modifier = modifier.size(44.dp),
        imageVector = VibePlayerIcons.SkipNext,
        iconDescription = stringResource(R.string.play_next),
        onClick = playNext,
    )
}

@Composable
fun VibePlayerPlayPreviousButton(
    modifier: Modifier = Modifier,
    playPrevious: () -> Unit
) {
    VibePlayerIconShape(
        modifier = modifier.size(44.dp),
        imageVector = VibePlayerIcons.SkipPrevious,
        iconDescription = stringResource(R.string.play_previous),
        onClick = playPrevious,
    )
}

@Composable
fun VibePlayerShuffleButton(
    modifier: Modifier = Modifier,
    shuffle: () -> Unit,
    isShuffled: Boolean,
) {
    VibePlayerIconShape(
        modifier = modifier.size(44.dp),
        containerColor = if (isShuffled) MaterialTheme.colorScheme.buttonHover else Color.Transparent,
        tintColor = if (isShuffled) MaterialTheme.colorScheme.textSecondary else MaterialTheme.colorScheme.textDisabled,
        imageVector = VibePlayerIcons.Shuffle,
        iconDescription = stringResource(R.string.shuffle),
        onClick = shuffle,
    )
}

@Composable
fun VibePlayerRepeatButton(
    modifier: Modifier = Modifier,
    repeatModeClick: () -> Unit,
    repeatMode: Int,
) {
    VibePlayerIconShape(
        modifier = modifier.size(44.dp),
        containerColor = if (repeatMode != REPEAT_MODE_OFF) MaterialTheme.colorScheme.buttonHover else Color.Transparent,
        tintColor = if (repeatMode != REPEAT_MODE_OFF) MaterialTheme.colorScheme.textSecondary else MaterialTheme.colorScheme.textDisabled,
        imageVector =
            when (repeatMode) {
                REPEAT_MODE_OFF -> VibePlayerIcons.RepeatOff
                REPEAT_MODE_ONE -> VibePlayerIcons.RepeatOne
                REPEAT_MODE_ALL -> VibePlayerIcons.RepeatAll
                else -> VibePlayerIcons.RepeatOff
            },
        iconDescription = stringResource(R.string.repeat_mode),
        onClick = repeatModeClick,
    )
}
