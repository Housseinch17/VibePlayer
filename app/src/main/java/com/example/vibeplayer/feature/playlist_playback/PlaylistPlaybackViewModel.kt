package com.example.vibeplayer.feature.playlist_playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.app.navigation.NavigationScreens
import com.example.vibeplayer.core.domain.PlaylistsWithSongsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PlaylistPlaybackEvents {
    data object NavigateBack : PlaylistPlaybackEvents
    data class AddSongs(val playlistName: String, val playlistId: Int) : PlaylistPlaybackEvents
    data class NavigateToNowPlaying(val nowPlayingData: NowPlayingData) : PlaylistPlaybackEvents
}

sealed interface PlaylistPlaybackActions {
    data object NavigateBack : PlaylistPlaybackActions
    data object AddSongs : PlaylistPlaybackActions
    data object NavigateAndPlay : PlaylistPlaybackActions
    data object NavigateAndShuffle : PlaylistPlaybackActions
}

class PlaylistPlaybackViewModel(
    private val navKey: NavigationScreens.PlaylistPlayback,
    private val playlistsWithSongsRepository: PlaylistsWithSongsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PlaylistPlaybackUiState())
    val state = _state.onStart {
        setPlaylist()
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        PlaylistPlaybackUiState()
    )

    private val _events = Channel<PlaylistPlaybackEvents>()
    val events = _events.receiveAsFlow()

    fun onActions(actions: PlaylistPlaybackActions) {
        when (actions) {
            PlaylistPlaybackActions.NavigateBack -> navigateBack()
            is PlaylistPlaybackActions.AddSongs -> navigateToAddSongs()
            PlaylistPlaybackActions.NavigateAndPlay -> navigateAndPlay()
            PlaylistPlaybackActions.NavigateAndShuffle -> navigateAndShuffle()
        }
    }

    private fun navigateAndPlay() {
        viewModelScope.launch {
            val playlistName = _state.value.playlist.playlistName
            _events.send(
                PlaylistPlaybackEvents.NavigateToNowPlaying(
                    NowPlayingData.PlayByPlaylist(
                        playlistName = playlistName
                    )
                )
            )
        }
    }

    private fun navigateAndShuffle() {
        viewModelScope.launch {
            val playlistName = _state.value.playlist.playlistName
            _events.send(
                PlaylistPlaybackEvents.NavigateToNowPlaying(
                    NowPlayingData.ShuffleByPlaylist(
                        playlistName = playlistName
                    )
                )
            )
        }
    }

    private fun navigateToAddSongs() {
        viewModelScope.launch {
            val playlistName = _state.value.playlist.playlistName
            val playlistId = _state.value.playlist.playlistId
            _events.send(
                PlaylistPlaybackEvents.AddSongs(
                    playlistName = playlistName,
                    playlistId = playlistId
                )
            )
        }
    }

    private fun setPlaylist() {
        viewModelScope.launch {
            val playlistName = navKey.playlistName
            playlistsWithSongsRepository.getPlaylistByName(playlistName = playlistName)
                .collect { (playlist, songs) ->
                    _state.update { newState ->
                        newState.copy(
                            playlist = playlist,
                            songs = songs
                        )
                    }
                }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(PlaylistPlaybackEvents.NavigateBack)
        }
    }


}