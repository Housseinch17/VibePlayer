package com.example.vibeplayer.feature.search.presentation

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import com.example.vibeplayer.feature.main.presentation.SongItem

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchUiState: SearchUiState,
    searchActions: (SearchActions) -> Unit,
) {
    Box(
        modifier = modifier.padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            SearchTopContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                searchQuery = searchUiState.searchQuery,
                updateSearchQuery = { newQuery ->
                    searchActions(SearchActions.UpdateSearchQuery(searchQuery = newQuery))
                },
                onClear = {
                    searchActions(SearchActions.Clear)
                },
                onCancel = {
                    searchActions(SearchActions.Cancel)
                }
            )

            if (searchUiState.searchQuery.isNotBlank()) {
                SearchList(
                    modifier = Modifier.fillMaxWidth(),
                    songList = searchUiState.songList,
                    onSongItemClick = { uri ->
                        searchActions(SearchActions.PlaySong(uri))
                    }
                )
            }
        }
        if (searchUiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.textSecondary
            )
        }
    }
}

@Composable
fun SearchTopContent(
    modifier: Modifier = Modifier,
    searchQuery: String,
    updateSearchQuery: (String) -> Unit,
    onClear: () -> Unit,
    onCancel: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester = focusRequester),
            value = searchQuery,
            onValueChange = { newQuery ->
                updateSearchQuery(newQuery)
            },
            shape = RoundedCornerShape(100.dp),
            leadingIcon = {
                Icon(
                    modifier = Modifier,
                    imageVector = VibePlayerIcons.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = LocalContentColor.current
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(
                        modifier = Modifier,
                        onClick = onClear
                    ) {
                        Icon(
                            modifier = Modifier,
                            imageVector = VibePlayerIcons.Clear,
                            contentDescription = stringResource(R.string.clear),
                            tint = LocalContentColor.current
                        )
                    }
                }
            },
            placeholder = {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.search),
                    style = MaterialTheme.typography.bodyLargeRegular.copy(
                        color = LocalContentColor.current
                    )
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.buttonHover,
                unfocusedContainerColor = MaterialTheme.colorScheme.buttonHover,
                focusedBorderColor = MaterialTheme.colorScheme.surfaceOutline,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceOutline,
                focusedTextColor = MaterialTheme.colorScheme.textPrimary,
                unfocusedTextColor = MaterialTheme.colorScheme.textPrimary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.textSecondary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.textSecondary,
                focusedTrailingIconColor = MaterialTheme.colorScheme.textSecondary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.textSecondary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.textSecondary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.textSecondary,
                cursorColor = MaterialTheme.colorScheme.textPrimary
            ),
            singleLine = true
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable {
                    onCancel()
                },
            text = stringResource(R.string.cancel),
            style = MaterialTheme.typography.bodyLargeMedium.copy(
                color = MaterialTheme.colorScheme.buttonPrimary
            )
        )
    }
}

@Composable
fun SearchList(
    modifier: Modifier = Modifier,
    songList: List<Song>,
    onSongItemClick: (Uri?) -> Unit,
) {
    if (songList.isEmpty()) {
        Text(
            modifier = modifier.padding(vertical = 8.dp),
            text = stringResource(R.string.no_results_found),
            style = MaterialTheme.typography.bodyMediumRegular
        )
    } else {
        val state = rememberLazyListState()
        LazyColumn(
            modifier = modifier,
            state = state,
        ) {
            items(
                items = songList,
                key = { song ->
                    song.id
                },
            ) { song ->
                SongItem(
                    modifier = Modifier,
                    song = song,
                    onSongItemClick = {
                        onSongItemClick(song.audioUri)
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.textSecondary
                )
            }
        }
    }
}

