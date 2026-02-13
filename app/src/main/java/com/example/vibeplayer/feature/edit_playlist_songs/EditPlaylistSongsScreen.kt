package com.example.vibeplayer.feature.edit_playlist_songs

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vibeplayer.R
import com.example.vibeplayer.app.navigation.NavigationScreens
import com.example.vibeplayer.core.data.Constants.FAVOURITE
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerAsyncImage
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonDestructive
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import com.example.vibeplayer.core.presentation.ui.ObserveAsEvents
import com.example.vibeplayer.feature.add_songs.SongItemWithClearButton
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditPlaylistSongsRoot(
    modifier: Modifier = Modifier,
    key: NavigationScreens.EditPlaylistSongs,
    navigateBack: () -> Unit,
) {
    //here to use the key in viewmodel we have to pass it in parametersOf
    val editPlaylistSongsViewModel = koinViewModel<EditPlaylistSongsViewModel> {
        parametersOf(key)
    }
    val editPlaylistUiState by editPlaylistSongsViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    ObserveAsEvents(editPlaylistSongsViewModel.events) { events ->
        when (events) {
            EditPlaylistSongsEvents.NavigateBack -> navigateBack()

            is EditPlaylistSongsEvents.ShowToast -> Toast.makeText(
                context,
                events.message.asString(context = context),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    EditPlaylistSongsScreen(
        modifier = modifier.fillMaxSize(),
        state = editPlaylistUiState,
        onActions = editPlaylistSongsViewModel::onActions
    )
}

@Composable
fun EditPlaylistSongsScreen(
    modifier: Modifier = Modifier,
    state: EditPlaylistSongsUiState,
    onActions: (EditPlaylistSongsActions) -> Unit
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
                    onActions(EditPlaylistSongsActions.NavigateBack)
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = innerPadding.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "${state.playlistName} ${stringResource(R.string.existing_songs)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.textPrimary,
                    textAlign = TextAlign.Center
                ),
                maxLines = 2,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(140.dp)
            ) {
                VibePlayerAsyncImage(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape),
                    imageUrl = state.playlistCoverPhoto,
                    errorDrawable = if (state.playlistName != FAVOURITE) {
                        R.drawable.other_playlist
                    } else {
                        R.drawable.favourite_playlist
                    },
                    contentDescription = stringResource(R.string.cover_photo)
                )
                if (state.canDelete) {
                    VibePlayerIconShape(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        imageVector = VibePlayerIcons.Delete,
                        iconDescription = stringResource(R.string.delete),
                        onClick = {
                            onActions(EditPlaylistSongsActions.DeleteCoverPhoto)
                        },
                        tintColor = MaterialTheme.colorScheme.buttonDestructive
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.textSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = rememberLazyListState()
            ) {
                if (state.existingSongs.isNotEmpty()) {
                    items(items = state.existingSongs, key = { it.id }) { existingSong ->
                        SongItemWithClearButton(
                            song = existingSong,
                            deleteSongFromPlaylist = {
                                onActions(EditPlaylistSongsActions.DeleteSong(id = existingSong.id))
                            }
                        )
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_songs_found),
                                style = MaterialTheme.typography.bodyLargeRegular
                            )
                        }
                    }
                }
            }
        }
    }
}