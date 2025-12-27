package com.example.vibeplayer.feature.now_playing.presentation

import com.example.vibeplayer.core.domain.MediaPlayerState
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.util.toMinutesSeconds

data class NowPlayingUiState(
    val song: Song = Song(),
    val progressIndicator: Float = 0f,
    val mediaPlayerState: MediaPlayerState = MediaPlayerState(),
    val playedOnceAtLeast: Boolean = false,
) {
    val valueRanged: ClosedFloatingPointRange<Float> = 0f..mediaPlayerState.duration.toFloat()
    val progressIndicatorForLinearProgress: Float =
        if (mediaPlayerState.duration > 0)
            (progressIndicator / mediaPlayerState.duration.toFloat())
                .coerceIn(0f, 1f)
        else 0f
    val sliderThumbText: String =
        "${(progressIndicator.toLong()).toMinutesSeconds()} / ${mediaPlayerState.duration.toMinutesSeconds()} "
}
