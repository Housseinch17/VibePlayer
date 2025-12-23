package com.example.vibeplayer.feature.scan_music.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.core.domain.SongRepository
import com.example.vibeplayer.core.presentation.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.vibeplayer.R
import com.example.vibeplayer.core.domain.Result
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

sealed interface ScanMusicEvents {
    data object NavigateBack : ScanMusicEvents
}

sealed interface ScanMusicActions {
    data class SelectDuration(val duration: String) : ScanMusicActions
    data class SelectSize(val size: String) : ScanMusicActions
    data object Scan : ScanMusicActions
    data object NavigateBack : ScanMusicActions
    data class UpdateSnackbarMessage(val snackbarMessage: UiText) : ScanMusicActions
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
            is ScanMusicActions.UpdateSnackbarMessage -> updateSnackbarMessage(snackbarMessage = scanMusicActions.snackbarMessage)
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
            val songResult = songRepository.scanAgain(
                duration = duration, size = size
            )
            val snackbarMessage = when (songResult) {
                is Result.Success -> {
                    UiText.StringResource(R.string.scan_complete, songResult.data.size)
                }

                else -> {
                    UiText.StringResource(R.string.scan_error)
                }
            }
            //delay for scanning
            delay(1.seconds)
            _scanMusicUi.update { newState ->
                newState.copy(
                    snackbarMessage = snackbarMessage,
                    isScanning = false
                )
            }
            //delay to show the snackbar
            delay(1.seconds)
            //navigate to MainPage
            navigateBack()
            _scanMusicUi.update { newState ->
                newState.copy(
                    snackbarMessage = null
                )
            }
        }
    }

    private fun updateSnackbarMessage(snackbarMessage: UiText?) {
        _scanMusicUi.update { newState ->
            newState.copy(
                snackbarMessage = snackbarMessage
            )
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _scanMusicEvents.send(ScanMusicEvents.NavigateBack)
        }
    }
}