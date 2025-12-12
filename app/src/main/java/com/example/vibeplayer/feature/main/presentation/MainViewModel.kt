package com.example.vibeplayer.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MainPageEvents {
    data object NavigateToScanMusic : MainPageEvents
}

sealed interface MainPageActions {
    data object ScanAgain : MainPageActions
    data object NavigateToScanMusic : MainPageActions
}

class MainViewModel(
    private val songRepository: SongRepository
) : ViewModel() {
    private val _mainPageUiState = MutableStateFlow(MainPageUiState())
    val mainPageUiState = _mainPageUiState.asStateFlow()

    private val _mainPageEvents = Channel<MainPageEvents>()
    val mainPageEvents = _mainPageEvents.receiveAsFlow()

    init {
        scanAllAudios()
    }

    fun onActions(mainPageActions: MainPageActions) {
        when (mainPageActions) {
            MainPageActions.ScanAgain -> scanAllAudios()
            MainPageActions.NavigateToScanMusic -> navigateToScanMusic()
        }
    }

    private fun scanAllAudios() {
        viewModelScope.launch {
            combine(
                flow = songRepository.getSongs(),
                flow2 = songRepository.syncSongsIfEmpty()
            ) { songs, _ ->
                songs
            }.collect { songs ->
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
}