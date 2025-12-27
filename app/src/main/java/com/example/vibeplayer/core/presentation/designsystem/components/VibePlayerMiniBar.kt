package com.example.vibeplayer.core.presentation.designsystem.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary

@Composable
fun VibePlayerMiniBar(
    modifier: Modifier = Modifier,
    onClick: (Uri?) -> Unit,
    song: Song,
    isPlaying: Boolean,
    play: () -> Unit,
    pause: () -> Unit,
    playNext: () -> Unit,
    progressIndicator: Float,
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceOutline,
                shape = RoundedCornerShape(
                    topStart = 12.dp, topEnd = 12.dp
                )
            )
            .shadow(
                elevation = 4.dp,
                ambientColor = Color(0x0A131D4D),
                spotColor = Color(0x0A131D4D)
            )
            .blur(12.dp)
            .clickable(
                onClick = {
                    onClick(song.audioUri)
                }
            )
            .padding(16.dp)

            .height(intrinsicSize = IntrinsicSize.Max),
        verticalAlignment = Alignment.Top,
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
                .fillMaxHeight()
                .padding(start = 12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TitleAndArtistName(
                    modifier = Modifier.weight(1f),
                    title = song.title,
                    artist = song.artist,
                )

                VibePlayerPlayButton(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp)
                        .size(44.dp),
                    isPlaying = isPlaying,
                    pause = pause,
                    play = play
                )

                VibePlayerPlayNextButton(
                    modifier = Modifier,
                    playNext = playNext
                )
            }

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                progress = { progressIndicator },
                color = MaterialTheme.colorScheme.textPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceOutline,
            )
        }
    }
}

@Composable
fun TitleAndArtistName(
    modifier: Modifier = Modifier,
    title: String,
    artist: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
        )
        Text(
            modifier = Modifier
                .padding(top = 2.dp)
                .horizontalScroll(rememberScrollState()),
            text = artist,
            style = MaterialTheme.typography.bodyMediumRegular.copy(
                color = MaterialTheme.colorScheme.textSecondary
            ),
            maxLines = 1,
        )
    }
}
