@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vibeplayer.feature.add_songs

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vibeplayer.R
import com.example.vibeplayer.app.navigation.NavigationScreens
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPrimaryButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerSearchField
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerSnackbar
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import com.example.vibeplayer.core.presentation.ui.ObserveAsEvents
import com.example.vibeplayer.feature.main.presentation.SongItem
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parameterSetOf

@Composable
fun AddSongsRoot(
    modifier: Modifier = Modifier,
    key: NavigationScreens.AddSong,
    navigateBack: () -> Unit,
) {
    val addSongsViewModel: AddSongsViewModel = koinViewModel<AddSongsViewModel> {
        parameterSetOf(key)
    }
    val addSongsUiState by addSongsViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    ObserveAsEvents(addSongsViewModel.events) { events ->
        when (events) {
            AddSongsEvents.NavigateBack -> navigateBack()
            is AddSongsEvents.ShowToast -> Toast.makeText(
                context,
                events.message.asString(context = context),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    AddSongsScreen(
        modifier = modifier.fillMaxSize(),
        state = addSongsUiState,
        onActions = addSongsViewModel::onActions
    )

}

@Composable
fun AddSongsScreen(
    modifier: Modifier = Modifier,
    state: AddSongsUiState,
    onActions: (AddSongsActions) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.snackbarMessage) {
        if (state.snackbarMessage != null) {
            snackBarHostState.showSnackbar(
                message = state.snackbarMessage.asString(context = context),
                withDismissAction = false
            )
        }
    }

    val lazyListState = rememberLazyListState()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = state.title.asString(),
                    style = MaterialTheme.typography.bodyLargeMedium.copy(
                        color = MaterialTheme.colorScheme.textPrimary,
                        textAlign = TextAlign.Center
                    )
                )
                VibePlayerIconShape(
                    modifier = Modifier,
                    imageVector = VibePlayerIcons.ArrowLeft,
                    iconDescription = stringResource(R.string.back),
                    onClick = {
                        onActions(AddSongsActions.NavigateBack)
                    },
                )
            }
        },
        bottomBar = {
            if (state.canSaveToDb) {
                VibePlayerPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = stringResource(R.string.ok),
                    isEnabled = !state.isSaving,
                    isScanning = state.isSaving,
                    onclick = {
                        onActions(AddSongsActions.SavePlaylist)
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    VibePlayerSnackbar(
                        snackbarData = data
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VibePlayerSearchField(
                modifier = Modifier.fillMaxWidth(),
                searchQuery = state.searchValue,
                updateSearchQuery = { newQuery ->
                    onActions(AddSongsActions.UpdateSearchValue(newQuery))
                },
                onClear = {
                    onActions(AddSongsActions.Clear)
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AddSongsRadioButton(
                    onClick = {
                        onActions(AddSongsActions.SelectAll)
                    },
                    selected = state.selectedAll
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.select_all),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                )
            }
            if (state.showNoSongsToAdd) {
                Text(
                    text = stringResource(R.string.no_songs_to_be_added),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.textSecondary
                    )
                )
            }
            if (!state.isLoading) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = lazyListState,
                ) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.textSecondary
                        )
                    }
                    items(
                        items = if (state.searchValue.isNotBlank()) state.filteredSongs else state.songsList,
                        key = { song ->
                            song.id
                        },
                    ) { song ->
                        val selectedSong = state.selectedIds.contains(
                            song
                                .id
                        )
                        SongItemWithRadioButton(
                            song = song,
                            selectedSong = selectedSong,
                            onRadioClick = {
                                onActions(AddSongsActions.SelectSong(song.id))
                            }
                        )
                    }
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(200.dp),
                    color = MaterialTheme.colorScheme.textSecondary
                )
            }
        }
    }
}

@Composable
fun SongItemWithClearButton(
    modifier: Modifier = Modifier,
    song: Song,
    deleteSongFromPlaylist: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            SongItem(
                modifier = Modifier.weight(1f),
                song = song,
                enabled = false,
            )
            Spacer(modifier = Modifier.width(12.dp))
            VibePlayerIconShape(
                imageVector = VibePlayerIcons.Clear,
                iconDescription = stringResource(R.string.clear),
                onClick = deleteSongFromPlaylist
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.textSecondary
        )
    }
}

@Composable
fun SongItemWithRadioButton(
    modifier: Modifier = Modifier,
    song: Song,
    selectedSong: Boolean,
    onRadioClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AddSongsRadioButton(
                onClick = onRadioClick,
                selected = selectedSong
            )
            Spacer(modifier = Modifier.width(12.dp))
            SongItem(
                modifier = Modifier.weight(1f),
                song = song,
                enabled = false,
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.textSecondary
        )
    }
}

@Composable
fun AddSongsRadioButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    selected: Boolean,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier,
            imageVector = if (selected) VibePlayerIcons.SelectedRadioButtonTick else VibePlayerIcons.UnSelectedRadioButton,
            contentDescription = stringResource(R.string.selected_ids),
            tint = Color.Unspecified
        )
    }
}

