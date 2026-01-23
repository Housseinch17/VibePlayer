package com.example.vibeplayer.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.core.domain.PlaybackController
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.domain.SongRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MainPageEvents {
    data object NavigateToScanMusic : MainPageEvents
    data class NavigateToNowPlaying(val nowPlayingData: NowPlayingData) : MainPageEvents
    data object NavigateToSearch : MainPageEvents
}

sealed interface MainPageActions {
    data object ScanAgain : MainPageActions
    data object NavigateToScanMusic : MainPageActions
    data class NavigateToNowPlaying(val nowPlayingData: NowPlayingData) : MainPageActions
    data object NavigateToSearch : MainPageActions
    data object NavigateAndPlay : MainPageActions
    data object NavigateAndShuffle : MainPageActions
    data object SetCurrentSong : MainPageActions
    data object Play : MainPageActions
    data object Pause : MainPageActions
    data object PlayNext : MainPageActions
    data object PlayPrevious : MainPageActions
    data class UpdateMainTabs(val mainTabs: MainTabs) : MainPageActions
}

class MainViewModel(
    private val songRepository: SongRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {
    private val _mainPageUiState = MutableStateFlow(MainPageUiState())
    val mainPageUiState = _mainPageUiState.onStart {
        viewModelScope.launch {
            initialSetup()
            setPlayerState()
            setProgressIndicator()
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        MainPageUiState()
    )
    private val _mainPageEvents = Channel<MainPageEvents>()
    val mainPageEvents = _mainPageEvents.receiveAsFlow()

    //after MainPage is destroyed the Exoplayer is not needed anymore
    override fun onCleared() {
        super.onCleared()
        playbackController.release()
    }

    fun onActions(mainPageActions: MainPageActions) {
        when (mainPageActions) {
            MainPageActions.ScanAgain -> scanAgain()
            MainPageActions.NavigateToScanMusic -> navigateToScanMusic()
            is MainPageActions.NavigateToNowPlaying -> {
                navigateToNowPlaying(nowPlayingData = mainPageActions.nowPlayingData)
            }

            is MainPageActions.UpdateMainTabs -> updateMainTabs(mainTabs = mainPageActions.mainTabs)
            MainPageActions.NavigateToSearch -> {
                navigateToSearch()
            }

            MainPageActions.NavigateAndPlay -> navigateAndPlay()
            MainPageActions.NavigateAndShuffle -> navigateAndShuffle()
            MainPageActions.SetCurrentSong -> setCurrentSongPlaying()
            MainPageActions.Pause -> pause()
            MainPageActions.Play -> play()
            MainPageActions.PlayNext -> playNext()
            MainPageActions.PlayPrevious -> playPrevious()
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

    private fun setProgressIndicator() {
        viewModelScope.launch {
            playbackController.currentProgressIndicator.collect { progressIndicator ->
                _mainPageUiState.update { newState ->
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
                _mainPageUiState.update { newState ->
                    newState.copy(
                        mediaPlayerState = mediaPlayerState,
                        currentSong = song ?: Song()
                    )
                }
            }
        }
    }

    private fun updateMainTabs(mainTabs: MainTabs) {
        _mainPageUiState.update { newState ->
            newState.copy(
                selectedMainTabs = mainTabs
            )
        }
    }

    private fun setCurrentSongPlaying() {
        viewModelScope.launch {
            val currentMediaItem = playbackController.currentMediaItem()
            currentMediaItem?.let { mediaItem ->
                val song = songRepository.getSongByMediaItem(mediaItem)
                _mainPageUiState.update { newState ->
                    newState.copy(
                        currentSong = song ?: Song()
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

    //used if state empty
    private fun scanAgain() {
        _mainPageUiState.update { newState ->
            newState.copy(
                songState = SongState.Scanning
            )
        }
        viewModelScope.launch {
            when (val scanAgain = songRepository.scanAgain()) {
                is Result.Success -> {
                    _mainPageUiState.update { newState ->
                        newState.copy(
                            songState = if (scanAgain.data.isEmpty()) SongState.Empty else SongState.TrackList(
                                songList = scanAgain.data
                            )
                        )
                    }
                }

                else -> {
                    _mainPageUiState.update { newState ->
                        newState.copy(
                            songState = SongState.Empty
                        )
                    }
                }
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
                if (songs.isNotEmpty()) {
                    playbackController.setPlayList(songs)
                }
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

    private fun navigateAndPlay() {
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToNowPlaying(NowPlayingData.Play))
        }
    }

    private fun navigateAndShuffle() {
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToNowPlaying(NowPlayingData.Shuffle))
        }
    }

    private fun navigateToScanMusic() {
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToScanMusic)
        }
    }

    private fun navigateToNowPlaying(nowPlayingData: NowPlayingData) {
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToNowPlaying(nowPlayingData = nowPlayingData))
        }
    }

    private fun navigateToSearch() {
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToSearch)
        }
    }
}