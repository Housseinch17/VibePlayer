package com.example.vibeplayer.features.vibePlayer.presentation.mainpage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.features.vibePlayer.presentation.mainpage.domain.AudioRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds


sealed interface MainPageEvents{
    data object NavigateToScanMusic: MainPageEvents
}
sealed interface MainPageActions {
    data object ScanAgain : MainPageActions
    data object NavigateToScanMusic: MainPageActions
}

class MainPageViewModel(
    private val audioRepository: AudioRepository
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
        val state = _mainPageUiState.value
        _mainPageUiState.update { newState ->
            newState.copy(
                audioState = AudioState.Scanning
            )
        }
        viewModelScope.launch {
            delay(5.seconds)
            val audioList = audioRepository.getAllAudios(
                duration = state.duration,
                size = state.size
            )
            Timber.tag("MyTag").d("audio: $audioList")
            _mainPageUiState.update { newState ->
                newState.copy(
                    audioState = if (audioList.isEmpty()) AudioState.Empty else AudioState.TrackList(
                        audioList
                    )
                )
            }
        }
    }

    private fun navigateToScanMusic(){
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToScanMusic)
        }
    }


}