package com.example.vibeplayer.features.vibePlayer.presentation.mainpage.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
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
import com.example.vibeplayer.core.presentation.designsystem.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.VibePlayerImages
import com.example.vibeplayer.core.presentation.designsystem.accent
import com.example.vibeplayer.core.presentation.designsystem.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.buttonPrimary
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerAsyncImage
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.components.rememberRotationEveryTwoSeconds
import com.example.vibeplayer.core.presentation.designsystem.surfaceBG
import com.example.vibeplayer.core.presentation.designsystem.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.textSecondary
import com.example.vibeplayer.core.presentation.ui.toMinutesSeconds
import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.data.model.Audio
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
                isClickable = mainPageUiState.audioState !is AudioState.Scanning,
                clickScan = {
                    onActions(MainPageActions.NavigateToScanMusic)
                }
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
            when (mainPageUiState.audioState) {
                AudioState.Empty -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        scanAgain = {
                            onActions(MainPageActions.ScanAgain)
                        })
                }

                AudioState.Scanning -> {
                    ScanningState(modifier = Modifier.fillMaxSize())
                }

                is AudioState.TrackList -> {
                    TrackListState(
                        modifier = Modifier,
                        state = lazyListState,
                        audioList = mainPageUiState.audioState.audioList
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
    audioList: List<Audio>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = state,
        ) {
            items(audioList, key = {
                it.id
            }) { audio ->

                AudioItem(
                    modifier = Modifier,
                    audio = audio
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
    val rotationEveryTwoSeconds = rememberRotationEveryTwoSeconds()
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
                textAlign = TextAlign.Center
            )
        )

        VibePlayerButton(
            modifier = Modifier.padding(top = 20.dp),
            onClick = scanAgain,
            buttonText = stringResource(R.string.scan_again)
        )
    }
}

@Composable
fun AudioItem(
    modifier: Modifier = Modifier,
    audio: Audio
) {
    Row(
        modifier = modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        VibePlayerAsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.small),
            imageUrl = audio.photoUri,
            errorDrawable = R.drawable.song_img_default,
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
                text = audio.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
            )
            Text(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .horizontalScroll(rememberScrollState()),
                text = audio.artist,
                style = MaterialTheme.typography.bodyMediumRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary
                ),
                maxLines = 1,
            )
        }

        Text(
            modifier = Modifier,
            text = audio.duration.toMinutesSeconds(),
            style = MaterialTheme.typography.bodyMediumRegular
        )
    }
}

@Composable
fun MainPageTopBar(
    modifier: Modifier = Modifier,
    isClickable: Boolean,
    clickScan: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainPageTextWithLeadingIcon(
            modifier = Modifier,
            imageVector = VibePlayerIcons.Logo,
            iconDescription = stringResource(R.string.logo_icon),
            text = stringResource(R.string.app_name)
        )

        VibePlayerIconShape(
            modifier = Modifier,
            imageVector = VibePlayerIcons.Scan,
            iconDescription = stringResource(R.string.scan),
            isClickable = isClickable,
            onClick = clickScan
        )
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