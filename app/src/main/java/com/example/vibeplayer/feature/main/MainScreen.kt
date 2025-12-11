package com.example.vibeplayer.feature.main

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vibeplayer.core.presentation.designsystem.buttons.VibePlayerPrimaryButton
import com.example.vibeplayer.core.presentation.designsystem.theme.SurfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerImages.RadarImage
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerTheme
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.feature.main.components.MainListItem
import com.example.vibeplayer.main.PreviewDataSource
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreenRoot(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainScreen(state)
}



@Composable
fun MainScreen(
    state: MainState = MainState()
) {
    VibePlayerTheme {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isLoading) {
                val infiniteTransition = rememberInfiniteTransition()

                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                Image(
                    painter = RadarImage,
                    contentDescription = "Loading",
                    modifier = Modifier
                        .size(124.dp)
                        .rotate(rotation)
                )

                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = "Scanning your device for music...",
                    style = MaterialTheme.typography.bodyMediumRegular
                )
            } else if (state.songs != null && state.songs.isEmpty()) {
                Text(
                    text = "No music found",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
                    text = "Try scanning again or check your folders.",
                    style = MaterialTheme.typography.bodyLargeMedium
                )
                VibePlayerPrimaryButton(
                    text = "Scan again"
                ) {

                }
            } else {
                state.songs?.let { songs ->
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        itemsIndexed(
                            items = songs,
                            key = { _, song -> song.id }
                        ) { index, song ->
                            MainListItem(song)
                            if (index < songs.lastIndex) {
                                HorizontalDivider(thickness = 1.dp, color = SurfaceOutline)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    VibePlayerTheme {
        MainScreen(MainState(isLoading = false, songs = PreviewDataSource.previewSongList))
    }
}