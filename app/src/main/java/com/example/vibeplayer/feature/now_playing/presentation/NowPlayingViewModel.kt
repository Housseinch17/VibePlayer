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

sealed interface NowPlayingEvents {
    data object Minimize : NowPlayingEvents
}

sealed interface NowPlayingActions {
    data object Minimize : NowPlayingActions
    data object Play : NowPlayingActions
    data object Pause : NowPlayingActions
    data object PlayNext : NowPlayingActions
    data object PlayPrevious : NowPlayingActions
    data object Shuffle : NowPlayingActions
    data object RepeatMode: NowPlayingActions
    data class SeekTo(val position: Long) : NowPlayingActions
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
            is NowPlayingActions.Minimize -> minimize()
            NowPlayingActions.Pause -> pause()
            NowPlayingActions.Play -> play()
            NowPlayingActions.PlayNext -> playNext()
            NowPlayingActions.PlayPrevious -> playPrevious()
            NowPlayingActions.Shuffle -> shuffle()
            NowPlayingActions.RepeatMode -> repeatMode()
            is NowPlayingActions.SeekTo -> seekTo(position = nowPlayingActions.position)
        }
    }

    private fun seekTo(position: Long) {
        playbackController.seekTo(position = position)
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
                        playbackController.setMediaItemByIndex(mediaItemsIndex = song.id - 1)
                    }
                }

                NowPlayingData.Play -> playByMediaOrder()

                NowPlayingData.Shuffle -> shuffle()
            }
        }
    }

    private fun playByMediaOrder() {
        playbackController.play(byMediaOrder = true)
    }

    private fun shuffle() {
        val isShuffled = _nowPlayingUiState.value.mediaPlayerState.isShuffled
        //if already shuffled means we have to unshuffle it so playByMediaOrder starts
        if (isShuffled) {
            playByMediaOrder()
        } else {
            playbackController.shuffle()
        }
    }

    private fun repeatMode() {
        playbackController.repeatMode()
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

    private fun minimize() {
        viewModelScope.launch {
            _nowPlayingEvents.send(NowPlayingEvents.Minimize)
        }
    }
}