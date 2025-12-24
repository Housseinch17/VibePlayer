package com.example.vibeplayer.feature.now_playing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.app.navigation.NavigationScreens
import com.example.vibeplayer.core.domain.PlaybackController
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface NowPlayingEvents {
    data object NavigateBack : NowPlayingEvents
}

sealed interface NowPlayingActions {
    data object NavigateBack : NowPlayingActions
    data object Play : NowPlayingActions
    data object Pause : NowPlayingActions
    data object PlayNext : NowPlayingActions
    data object PlayPrevious : NowPlayingActions
}

class NowPlayingViewModel(
    val navKey: NavigationScreens.NowPlaying,
    private val songRepository: SongRepository,
    private val playbackController: PlaybackController,
) : ViewModel(

) {
    private val _nowPlayingUiState = MutableStateFlow(NowPlayingUiState())
    val nowPlayingUiState = _nowPlayingUiState.onStart {
        setPlayerState()
        setProgressIndicator()
        setInitialSong()
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        NowPlayingUiState()
    )

    private val _nowPlayingEvents = Channel<NowPlayingEvents>()
    val nowPlayingEvents = _nowPlayingEvents.receiveAsFlow()

    fun onActions(nowPlayingActions: NowPlayingActions) {
        when (nowPlayingActions) {
            NowPlayingActions.NavigateBack -> navigateBack()
            NowPlayingActions.Pause -> pause()
            NowPlayingActions.Play -> play()
            NowPlayingActions.PlayNext -> playNext()
            NowPlayingActions.PlayPrevious -> playPrevious()
        }
    }

    //if the user use back from actions
//stop playbackController
//note: after using stop() if we want to play a song again we have to ensure that prepare() is used
    override fun onCleared() {
        super.onCleared()
        playbackController.stop()
    }

    private fun setInitialSong() {
        viewModelScope.launch {
            when (navKey.nowPlayingData) {
                is NowPlayingData.PlayByUri -> {
                    val uri = navKey.nowPlayingData.uri
                    val song = songRepository.getSongByUri(uri = uri)
                    _nowPlayingUiState.update { newState ->
                        newState.copy(
                            song = song ?: Song(),
                        )
                    }
                    song?.let {
                        Timber.tag("MyTag").d("started: ${song.id}")
                        playbackController.setMediaItemByIndex(mediaItemsIndex = song.id - 1)
                    }
                }

                NowPlayingData.Play -> playbackController.play(byMediaOrder = true)
                NowPlayingData.Shuffle -> playbackController.shuffle()
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

    private fun setPlayerState() {
        viewModelScope.launch {
            playbackController.mediaPlayerState.collect { mediaPlayerState ->
                val song =
                    songRepository.getSongByMediaItem(mediaItem = mediaPlayerState.currentMedia)
                _nowPlayingUiState.update { newState ->
                    newState.copy(
                        mediaPlayerState = mediaPlayerState,
                        song = song ?: Song()
                    )
                }
            }
        }
    }

    private fun play() {
        playbackController.play()
    }

    private fun playNext() {
        playbackController.seekToNext()
    }

    private fun playPrevious() {
        playbackController.seekToPrevious()
    }

    private fun pause() {
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