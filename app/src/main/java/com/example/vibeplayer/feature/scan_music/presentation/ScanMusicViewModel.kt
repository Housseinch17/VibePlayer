package com.example.vibeplayer.feature.scan_music.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

sealed interface ScanMusicEvents {
    data object NavigateBack : ScanMusicEvents
}

sealed interface ScanMusicActions {
    data class SelectDuration(val duration: String) : ScanMusicActions
    data class SelectSize(val size: String) : ScanMusicActions
    data object Scan : ScanMusicActions
    data object NavigateBack : ScanMusicActions
}

class ScanMusicViewModel(
    private val songRepository: SongRepository
) : ViewModel() {
    private val _scanMusicUi = MutableStateFlow(ScanMusicUi())
    val scanMusicUi = _scanMusicUi.asStateFlow()

    private val _scanMusicEvents = Channel<ScanMusicEvents>()
    val scanMusicEvents = _scanMusicEvents.receiveAsFlow()

    fun onActions(scanMusicActions: ScanMusicActions) {
        when (scanMusicActions) {
            is ScanMusicActions.SelectDuration -> selectDuration(duration = scanMusicActions.duration)
            is ScanMusicActions.SelectSize -> selectSize(size = scanMusicActions.size)
            ScanMusicActions.Scan -> scan()
            ScanMusicActions.NavigateBack -> navigateBack()
        }
    }

    private fun selectDuration(duration: String) {
        _scanMusicUi.update { newState ->
            newState.copy(
                selectedDuration = duration
            )
        }
    }

    private fun selectSize(size: String) {
        _scanMusicUi.update { newState ->
            newState.copy(
                selectedSize = size
            )
        }
    }

    private fun scan() {
        _scanMusicUi.update { newState ->
            newState.copy(
                isScanning = true
            )
        }
        viewModelScope.launch {
            val duration = _scanMusicUi.value.selectedDuration.toLong()
            val size = _scanMusicUi.value.selectedSize.toLong()
            delay(3.seconds)

            songRepository.scanAgain(
                duration = duration,
                size = size
            )
            _scanMusicUi.update { newState ->
                newState.copy(
                    isScanning = false
                )
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _scanMusicEvents.send(ScanMusicEvents.NavigateBack)
        }
    }
}