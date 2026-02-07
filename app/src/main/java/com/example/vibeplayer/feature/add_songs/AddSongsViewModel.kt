package com.example.vibeplayer.feature.add_songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.R
import com.example.vibeplayer.app.navigation.NavigationScreens
import com.example.vibeplayer.core.domain.PlaylistsWithSongsRepository
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.domain.SongRepository
import com.example.vibeplayer.core.presentation.ui.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

sealed interface AddSongsEvents {
    data object NavigateBack : AddSongsEvents
    data class ShowToast(val message: UiText) : AddSongsEvents
}

sealed interface AddSongsActions {
    data class UpdateSearchValue(val searchValue: String) : AddSongsActions
    data object NavigateBack : AddSongsActions
    data object SelectAll : AddSongsActions
    data class SelectSong(val id: Int) : AddSongsActions
    data object Clear : AddSongsActions
    data object SavePlaylist : AddSongsActions
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AddSongsViewModel(
    private val navKey: NavigationScreens.AddSong,
    private val songRepository: SongRepository,
    private val playlistsWithSongsRepository: PlaylistsWithSongsRepository,
    private val applicationScope: CoroutineScope,
) : ViewModel() {
    private val _state = MutableStateFlow(AddSongsUiState())
    val state = _state.onStart {
        //both functions are suspend because setSongs() should wait setPlaylistNameAndId() to finish
        //before executing so setPlaylistName isn't launched in any new scope/thread
        setPlaylistNameAndId()
        setSongs()
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        AddSongsUiState()
    )

    private val _events = Channel<AddSongsEvents>()
    val events = _events.receiveAsFlow()

    override fun onCleared() {
        super.onCleared()
        val playlistName = _state.value.playlistName
        applicationScope.launch {
            val playlistAlreadyExist =
                playlistsWithSongsRepository.playlistAlreadyExists(playlistName = playlistName)
            if (!playlistAlreadyExist) {
                playlistsWithSongsRepository.saveEmptyPlaylist(playlistName = playlistName)
            }
        }
    }

    fun onActions(addSongsActions: AddSongsActions) {
        when (addSongsActions) {
            AddSongsActions.NavigateBack -> navigateBack()
            AddSongsActions.SelectAll -> selectAll()
            AddSongsActions.Clear -> clear()
            AddSongsActions.SavePlaylist -> savePlaylist()
            is AddSongsActions.SelectSong -> selectSong(selectedId = addSongsActions.id)
            is AddSongsActions.UpdateSearchValue -> updateSearchValue(searchValue = addSongsActions.searchValue)
        }
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

    private fun setSongs() {
        viewModelScope.launch {
            val playlistName = _state.value.playlistName
            val playlistAlreadyExists =
                playlistsWithSongsRepository.playlistAlreadyExists(playlistName = playlistName)
            if (playlistAlreadyExists) {
                combine(
                    flow = playlistsWithSongsRepository.getPlaylistByName(playlistName = playlistName),
                    flow2 = songRepository.getSongs()
                ) { domain, songs ->
                    val playlistExistingSongsId = domain.songs.map { it.id }
                    val remainingSongs = songs.filter {
                        it.id !in playlistExistingSongsId
                    }
                    remainingSongs
                }.collect { remainingSongs ->
                    _state.update { newState ->
                        newState.copy(
                            songsList = remainingSongs,
                        )
                    }
                }
            } else {
                songRepository.getSongs().collect { songs->
                    _state.update { newState->
                        newState.copy(
                            songsList = songs
                        )
                    }
                }
            }
        }
    }

    private fun savePlaylist() {
        viewModelScope.launch {
            _state.update { newState ->
                newState.copy(
                    isSaving = true,
                )
            }
            val playlistName = _state.value.playlistName
            val selectedSongsId = _state.value.selectedIds
            val playlistAlreadyExist =
                playlistsWithSongsRepository.playlistAlreadyExists(playlistName = playlistName)
            val saveOrUpdatePlaylist =
                if (playlistAlreadyExist) playlistsWithSongsRepository.addSongsToExistingPlaylist(
                    playlistName = playlistName,
                    selectedSongIds = selectedSongsId
                ) else playlistsWithSongsRepository.createPlaylistWithSongs(
                    playlistName = playlistName,
                    selectedSongIds = selectedSongsId
                )
            when (saveOrUpdatePlaylist) {
                is Result.Error -> showSnackbarMessage(saveOrUpdatePlaylist.exception)
                is Result.Success -> {
                    showSnackbarMessage(
                        UiText.StringResource(
                            R.string.songs_added_to_playlist,
                            _state.value.selectedIds.size
                        )
                    )
                    _events.send(
                        AddSongsEvents.NavigateBack
                    )
                }
            }
            _state.update { newState ->
                newState.copy(
                    isSaving = false
                )
            }
        }
    }

    private fun updateSearchValue(searchValue: String) {
        _state.update { newState ->
            newState.copy(
                searchValue = searchValue
            )
        }
        filterSongs(searchValue = searchValue)
    }

    private fun filterSongs(searchValue: String) {
        if (searchValue.isNotBlank()) {
            viewModelScope.launch {
                _state.update { newState ->
                    newState.copy(
                        isLoading = true
                    )
                }
                val filteredSong = _state.value.songsList.filter {
                    it.artist.contains(searchValue, true) || it.title.contains(searchValue, true)
                }
                _state.update { newState ->
                    newState.copy(
                        filteredSongs = filteredSong,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun selectSong(selectedId: Int) {
        val alreadySelected = _state.value.selectedIds.contains(selectedId)
        _state.update { newState ->
            newState.copy(
                selectedIds = if (!alreadySelected) newState.selectedIds + selectedId else newState.selectedIds - selectedId
            )
        }
    }

    private fun selectAll() {
        if (_state.value.selectedAll) {
            _state.update { newState ->
                newState.copy(
                    selectedIds = emptyList()
                )
            }
            return
        }
        val songsIds = _state.value.songsList.map {
            it.id
        }
        _state.update { newState ->
            newState.copy(
                selectedIds = songsIds
            )
        }
    }

    private fun showSnackbarMessage(snackbarMessage: UiText?) {
        viewModelScope.launch {
            _state.update { newState ->
                newState.copy(
                    snackbarMessage = snackbarMessage
                )
            }
            delay(1.seconds)
            _state.update { newState ->
                newState.copy(
                    snackbarMessage = null
                )
            }
        }
    }


    private fun clear() {
        _state.update { newState ->
            newState.copy(
                searchValue = ""
            )
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(AddSongsEvents.NavigateBack)
        }
    }
}