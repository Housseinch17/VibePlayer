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
import com.example.vibeplayer.app.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.buttons.VibePlayerPrimaryButton
import com.example.vibeplayer.core.presentation.designsystem.theme.SurfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerImages.RadarImage
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerTheme
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.feature.main.components.MainListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreenRoot(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainScreen(state)
}

val dummySongs = listOf(
    Song(
        id = 1,
        title = "Midnight Drive",
        artist = "Neon Skyline",
        filePath = "/storage/emulated/0/Music/midnight_drive.mp3",
        duration = 198000L // 3:18
    ),
    Song(
        id = 2,
        title = "Ocean Breeze",
        artist = "Blue Horizon",
        filePath = "/storage/emulated/0/Music/ocean_breeze.mp3",
        duration = 242000L // 4:02
    ),
    Song(
        id = 3,
        title = "Golden Hour",
        artist = "Sunset Avenue",
        filePath = "/storage/emulated/0/Music/golden_hour.mp3",
        duration = 215000L // 3:35
    ),
    Song(
        id = 4,
        title = "Lost in Space",
        artist = "Stellar Drift",
        filePath = "/storage/emulated/0/Music/lost_in_space.mp3",
        duration = 185000L // 3:05
    ),
    Song(
        id = 5,
        title = "Echoes",
        artist = "Nova Lights",
        filePath = "/storage/emulated/0/Music/echoes.mp3",
        duration = 227000L // 3:47
    ),
    Song(
        id = 6,
        title = "Waves",
        artist = "Calm Shores",
        filePath = "/storage/emulated/0/Music/waves.mp3",
        duration = 264000L // 4:24
    ),
    Song(
        id = 7,
        title = "City Nights",
        artist = "Urban Echo",
        filePath = "/storage/emulated/0/Music/city_nights.mp3",
        duration = 201000L // 3:21
    ),
    Song(
        id = 8,
        title = "Shadow Walk",
        artist = "Dark Lantern",
        filePath = "/storage/emulated/0/Music/shadow_walk.mp3",
        duration = 233000L // 3:53
    ),
    Song(
        id = 9,
        title = "Running Free",
        artist = "Open Road",
        filePath = "/storage/emulated/0/Music/running_free.mp3",
        duration = 178000L // 2:58
    ),
    Song(
        id = 10,
        title = "Dreamscape",
        artist = "Cloud Harbor",
        filePath = "/storage/emulated/0/Music/dreamscape.mp3",
        duration = 251000L // 4:11
    )
)

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
        MainScreen(MainState(isLoading = false, songs = dummySongs))
    }
}