package com.example.vibeplayer.feature.search.presentation

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vibeplayer.R
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerSearchField
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import com.example.vibeplayer.core.presentation.ui.ObserveAsEvents
import com.example.vibeplayer.feature.main.presentation.SongItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchRoot(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = koinViewModel<SearchViewModel>(),
    navigateBack: () -> Unit,
    navigateToNowPlaying: (NowPlayingData) -> Unit,
    ) {
    val searchUiState by searchViewModel.searchUiState.collectAsStateWithLifecycle()

    ObserveAsEvents(searchViewModel.searchEvents) { events ->
        when (events) {
            SearchEvents.NavigateBack -> navigateBack()
            is SearchEvents.NavigateToNowPlaying -> {
                navigateToNowPlaying(events.nowPlayingData)
            }
        }
    }

    SearchScreen(
        modifier = modifier.fillMaxSize(),
        searchUiState = searchUiState,
        searchActions = searchViewModel::onActions
    )
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchUiState: SearchUiState,
    searchActions: (SearchActions) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
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
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VibePlayerSearchField(
            modifier = Modifier.weight(1f),
            searchQuery = searchQuery,
            updateSearchQuery = { newQuery ->
                updateSearchQuery(newQuery)
            },
            onClear = onClear
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
    onSongItemClick: (Int) -> Unit,
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
                        onSongItemClick(song.id)
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

