package com.example.vibeplayer.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MainPageEvents {
    data object NavigateToScanMusic : MainPageEvents
    data class NavigateToNowPlaying(val songId: Long) : MainPageEvents
}

sealed interface MainPageActions {
    data object ScanAgain : MainPageActions
    data object NavigateToScanMusic : MainPageActions
    data class NavigateToNowPlaying(val songId: Long) : MainPageActions
}

class MainViewModel(
    private val songRepository: SongRepository
) : ViewModel() {
    private val _mainPageUiState = MutableStateFlow(MainPageUiState())
    val mainPageUiState = _mainPageUiState.asStateFlow()

    private val _mainPageEvents = Channel<MainPageEvents>()
    val mainPageEvents = _mainPageEvents.receiveAsFlow()

    init {
        initialSetup()
    }

    fun onActions(mainPageActions: MainPageActions) {
        when (mainPageActions) {
            MainPageActions.ScanAgain -> scanAgain()
            MainPageActions.NavigateToScanMusic -> navigateToScanMusic()
            is MainPageActions.NavigateToNowPlaying -> navigateToNowPlaying(songId = mainPageActions.songId)
        }
    }

    private fun initialSetup() {
        viewModelScope.launch {
            //syncSongsIfEmpty will only work if room db is empty
            //so if room is empty cleanUpRemovedSongs will be useless
            //if room not empty syncSongsIfEmpty will not work
            //cleanUpRemovedSongs will work
            async {
                syncSongsIfEmpty()
                cleanUpRemovedSongs()
            }.await()
            getSongs()
        }
    }

    private fun scanAgain() {
        _mainPageUiState.update { newState ->
            newState.copy(
                songState = SongState.Scanning
            )
        }
        viewModelScope.launch {
            when(val scanAgain = songRepository.scanAgain()){
                is Result.Success -> {
                    _mainPageUiState.update { newState ->
                        newState.copy(
                            songState = if (scanAgain.data.isEmpty()) SongState.Empty else SongState.TrackList(
                                songList = scanAgain.data
                            )
                        )
                    }
                }

                else -> {  _mainPageUiState.update { newState ->
                    newState.copy(
                        songState =  SongState.Empty
                    )
                }}
            }

        }
    }

    private suspend fun syncSongsIfEmpty() {
        songRepository.syncSongsIfEmpty()
    }

    private suspend fun cleanUpRemovedSongs() {
        songRepository.cleanUpRemovedSongs()
    }

    private fun getSongs() {
        viewModelScope.launch {
            songRepository.getSongs().collect { songs ->
                _mainPageUiState.update { newState ->
                    newState.copy(
                        songState = if (songs.isEmpty()) SongState.Empty else SongState.TrackList(
                            songList = songs
                        )
                    )
                }
            }
        }
    }

    private fun navigateToScanMusic() {
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToScanMusic)
        }
    }

    private fun navigateToNowPlaying(songId: Long) {
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToNowPlaying(songId = songId))
        }
    }
}