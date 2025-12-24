@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.example.vibeplayer.feature.search.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SearchEvents {
    data object NavigateBack : SearchEvents
    data class NavigateToNowPlaying(val nowPlayingData: NowPlayingData) : SearchEvents
}

sealed interface SearchActions {
    data class UpdateSearchQuery(val searchQuery: String) : SearchActions
    data object Clear : SearchActions
    data object Cancel : SearchActions
    data class PlaySong(val uri: Uri?) : SearchActions
}

class SearchViewModel(
    private val songRepository: SongRepository
) : ViewModel() {
    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState = _searchUiState.onStart {
        viewModelScope.launch {
        _searchUiState.map {
            it.searchQuery
        }.debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank())
                    flowOf(emptyList())
                else
                    songRepository.getSongsByTitleOrArtistName(searchQuery = query)
            }.collect { songs ->
                _searchUiState.update { newState ->
                    newState.copy(
                        songList = songs,
                        isLoading = false,
                    )
                }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        SearchUiState()
    )

    private val _searchEvents = Channel<SearchEvents>()
    val searchEvents = _searchEvents.receiveAsFlow()

    fun onActions(searchActions: SearchActions) {
        when (searchActions) {
            is SearchActions.PlaySong -> playSong(uri = searchActions.uri)
            is SearchActions.UpdateSearchQuery -> updateSearchQuery(searchActions.searchQuery)
            SearchActions.Clear -> clear()
            SearchActions.Cancel -> cancel()
        }
    }

    private fun updateSearchQuery(searchQuery: String) {
        _searchUiState.update { newState ->
            newState.copy(
                searchQuery = searchQuery,
                isLoading = true
            )
        }
    }

    private fun clear() {
        _searchUiState.update { newState ->
            newState.copy(
                searchQuery = ""
            )
        }
    }

    private fun cancel() {
        viewModelScope.launch {
            _searchEvents.send(SearchEvents.NavigateBack)
        }
    }

    private fun playSong(uri: Uri?) {
        viewModelScope.launch {
            _searchEvents.send(
                SearchEvents.NavigateToNowPlaying(
                    nowPlayingData = NowPlayingData.PlayByUri(
                        uri
                    )
                )
            )
        }
    }

}