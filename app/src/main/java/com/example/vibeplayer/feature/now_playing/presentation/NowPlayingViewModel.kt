package com.example.vibeplayer.feature.now_playing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.app.presentation.navigation.NavigationScreens
import com.example.vibeplayer.core.domain.PlaybackController
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface NowPlayingEvents {
    data object NavigateBack : NowPlayingEvents
}

sealed interface NowPlayingActions {
    data object NavigateBack : NowPlayingActions
    data object Play : NowPlayingActions
    data object Pause : NowPlayingActions
    data object PlayNextSong : NowPlayingActions
    data object PlayPreviousSong : NowPlayingActions
}

class NowPlayingViewModel(
    val navKey: NavigationScreens.NowPlaying,
    private val songRepository: SongRepository,
    private val playbackController: PlaybackController,
) : ViewModel(

) {
    private val _nowPlayingUiState = MutableStateFlow(NowPlayingUiState())
    val nowPlayingUiState = _nowPlayingUiState.asStateFlow()

    init {
        setSong()
        setProgressIndicator()
    }

    private val _nowPlayingEvents = Channel<NowPlayingEvents>()
    val nowPlayingEvents = _nowPlayingEvents.receiveAsFlow()

    fun onActions(nowPlayingActions: NowPlayingActions) {
        when (nowPlayingActions) {
            NowPlayingActions.NavigateBack -> navigateBack()
            NowPlayingActions.Pause -> pause()
            NowPlayingActions.Play -> play()
            NowPlayingActions.PlayNextSong -> playNextSong()
            NowPlayingActions.PlayPreviousSong -> playPreviousSong()
        }
    }

    //if the user use back from actions
    //stop playbackController
    override fun onCleared() {
        super.onCleared()
        playbackController.stop()
    }

    private fun setSong() {
        viewModelScope.launch {
            val id = navKey.id
            val song = songRepository.getSongById(id = id)
            _nowPlayingUiState.update { newState ->
                newState.copy(
                    song = song,
                )
            }
        }
    }

    private fun setProgressIndicator() {
        viewModelScope.launch {
            playbackController.currentProgressIndicator.collect { progressIndicator ->
                _nowPlayingUiState.update { newState ->
                    newState.copy(
                        progressIndicator = progressIndicator
                    )
                }
            }
        }
    }

    private fun play() {
        _nowPlayingUiState.update { newState ->
            newState.copy(
                isPlaying = true
            )
        }
        val audioUri = _nowPlayingUiState.value.song.audioUri
        playbackController.play(audioUri = audioUri!!)
    }

    private fun playNextSong() {
        _nowPlayingUiState.update { newState ->
            newState.copy(
                isPlaying = false
            )
        }
        viewModelScope.launch {
            //stop existing if any
            playbackController.stop()
            val nextId = _nowPlayingUiState.value.song.id + 1
            val nextSong = songRepository.getNextSong(nextId = nextId)
            nextSong?.audioUri?.let { nextAudioUri ->
                _nowPlayingUiState.update { newState ->
                    newState.copy(
                        isPlaying = true,
                        song = nextSong,
                    )
                }
                playbackController.playNext(audioUri = nextAudioUri)
            }
        }
    }

    private fun playPreviousSong() {
        _nowPlayingUiState.update { newState ->
            newState.copy(
                isPlaying = false
            )
        }
        viewModelScope.launch {
            //stop existing if any
            playbackController.stop()
            val previousId = _nowPlayingUiState.value.song.id - 1
            val previousSong = songRepository.getPreviousSong(previousId = previousId)
            previousSong?.audioUri?.let { previousAudioUri ->
                _nowPlayingUiState.update { newState ->
                    newState.copy(
                        isPlaying = true,
                        song = previousSong
                    )
                }
                playbackController.playNext(audioUri = previousAudioUri)
            }
        }
    }

    private fun pause() {
        _nowPlayingUiState.update { newState ->
            newState.copy(
                isPlaying = false
            )
        }
        playbackController.pause()
    }

    private fun navigateBack() {
        viewModelScope.launch {
            //stop before navigating
            playbackController.stop()
            _nowPlayingEvents.send(NowPlayingEvents.NavigateBack)
        }
    }

}