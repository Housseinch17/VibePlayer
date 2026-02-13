package com.example.vibeplayer.feature.edit_playlist_songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.R
import com.example.vibeplayer.app.navigation.NavigationScreens
import com.example.vibeplayer.core.domain.PlaylistsWithSongsRepository
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.presentation.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface EditPlaylistSongsEvents {
    data class ShowToast(val message: UiText) : EditPlaylistSongsEvents
    data object NavigateBack : EditPlaylistSongsEvents
}

sealed interface EditPlaylistSongsActions {
    data class DeleteSong(val id: Int) : EditPlaylistSongsActions
    data object NavigateBack : EditPlaylistSongsActions
    data object DeleteCoverPhoto : EditPlaylistSongsActions
}

class EditPlaylistSongsViewModel(
    private val navKey: NavigationScreens.EditPlaylistSongs,
    private val playlistsWithSongsRepository: PlaylistsWithSongsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(EditPlaylistSongsUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<EditPlaylistSongsEvents>()
    val events = _events.receiveAsFlow()

    init {
        setPlaylistNameAndId()
        setExistingSongs()
    }

    private fun setPlaylistNameAndId() {
        val playlistName = navKey.playlistName
        val playlistId = navKey.playlistId
        _state.update { newState ->
            newState.copy(
                playlistName = playlistName,
                playlistId = playlistId
            )
        }
    }

    private fun setExistingSongs() {
        viewModelScope.launch {
            val playlistName = navKey.playlistName
            playlistsWithSongsRepository.getPlaylistByName(
                playlistName = playlistName
            ).collect { playlistWithSongsDomain ->
                _state.update { newState ->
                    newState.copy(
                        existingSongs = playlistWithSongsDomain.songs,
                        playlistCoverPhoto = playlistWithSongsDomain.playlist.embeddedUri
                            ?: playlistWithSongsDomain.songs.firstOrNull()?.embeddedArt,
                        canDelete = playlistWithSongsDomain.playlist.embeddedUri != null
                    )
                }
            }
        }
    }

    fun onActions(actions: EditPlaylistSongsActions) {
        when (actions) {
            is EditPlaylistSongsActions.DeleteSong -> deleteSong(id = actions.id)
            EditPlaylistSongsActions.NavigateBack -> navigateBack()
            EditPlaylistSongsActions.DeleteCoverPhoto -> deleteCoverPhoto()
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(EditPlaylistSongsEvents.NavigateBack)
        }
    }

    private fun deleteCoverPhoto() {
        viewModelScope.launch {
            val playlistId = _state.value.playlistId
            val deleteCoverPhotoResult =
                playlistsWithSongsRepository.deleteCoverPhoto(playlistId = playlistId)
            when (deleteCoverPhotoResult) {
                is Result.Error -> {
                    _events.send(
                        EditPlaylistSongsEvents.ShowToast(
                            deleteCoverPhotoResult.exception
                        )
                    )
                }

                is Result.Success -> {
                    _events.send(
                        EditPlaylistSongsEvents.ShowToast(
                            UiText.StringResource(
                                R.string.cover_photo_delete
                            )
                        )
                    )
                }
            }
        }
    }

    private fun deleteSong(id: Int) {
        viewModelScope.launch {
            val playlistId = _state.value.playlistId
            val deleteSongResult = playlistsWithSongsRepository.deleteSongFromPlaylistById(
                playlistId = playlistId,
                songDbId = id
            )
            when (deleteSongResult) {
                is Result.Error -> _events.send(
                    EditPlaylistSongsEvents.ShowToast(
                        deleteSongResult.exception
                    )
                )

                is Result.Success -> _events.send(
                    EditPlaylistSongsEvents.ShowToast(
                        UiText.StringResource(
                            R.string.song_deleted
                        )
                    )
                )
            }
        }
    }
}