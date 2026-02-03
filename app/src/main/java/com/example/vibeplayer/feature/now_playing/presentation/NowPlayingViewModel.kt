package com.example.vibeplayer.feature.now_playing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.R
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.app.navigation.NavigationScreens
import com.example.vibeplayer.core.data.Constants.FAVOURITE
import com.example.vibeplayer.core.domain.PlaybackController
import com.example.vibeplayer.core.domain.PlaylistsWithSongsRepository
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.domain.SongRepository
import com.example.vibeplayer.core.presentation.ui.UiText
import com.example.vibeplayer.feature.main.presentation.PlayListModel
import kotlinx.coroutines.async
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
    data class ShowToast(val message: UiText) : NowPlayingEvents
}

sealed interface NowPlayingActions {
    data object Minimize : NowPlayingActions
    data object PlaylistClick : NowPlayingActions
    data object FavouriteClick : NowPlayingActions
    data object Play : NowPlayingActions
    data object Pause : NowPlayingActions
    data object PlayNext : NowPlayingActions
    data object PlayPrevious : NowPlayingActions
    data object Shuffle : NowPlayingActions
    data object RepeatMode : NowPlayingActions
    data class SeekTo(val position: Long) : NowPlayingActions
    data object HideBottomSheet : NowPlayingActions
    data object HideCreatePlaylistBottomSheet : NowPlayingActions
    data object ShowCreatePlaylist : NowPlayingActions
    data class UpdatePlaylistTextField(val value: String) : NowPlayingActions
    data object SaveToNewPlaylist : NowPlayingActions
    data class SaveToExistingPlaylist(val playlistName: String) : NowPlayingActions
}

class NowPlayingViewModel(
    val navKey: NavigationScreens.NowPlaying,
    private val songRepository: SongRepository,
    private val playbackController: PlaybackController,
    private val playlistsWithSongsRepository: PlaylistsWithSongsRepository
) : ViewModel() {
    private val _nowPlayingUiState = MutableStateFlow(NowPlayingUiState())
    val nowPlayingUiState = _nowPlayingUiState.onStart {
        setPlayerState()
        setProgressIndicator()
        setInitialSong()
        setPlaylists()
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
            NowPlayingActions.FavouriteClick -> onFavouriteClick()
            NowPlayingActions.PlaylistClick -> onPlaylistClick()
            NowPlayingActions.HideBottomSheet -> hideBottomSheet()
            NowPlayingActions.ShowCreatePlaylist -> showCreatePlaylist()
            is NowPlayingActions.UpdatePlaylistTextField -> updatePlaylistTextField(newValue = nowPlayingActions.value)
            NowPlayingActions.HideCreatePlaylistBottomSheet -> hideCreatePlaylist()
            NowPlayingActions.SaveToNewPlaylist -> saveToNewPlaylist()
            is NowPlayingActions.SaveToExistingPlaylist -> saveToExistingPlaylist(playlistName = nowPlayingActions.playlistName)
        }
    }

    private fun saveToExistingPlaylist(playlistName: String) {
        viewModelScope.launch {
            _nowPlayingUiState.update { newState ->
                newState.copy(
                    isSaving = true
                )
            }
            val songDbId = _nowPlayingUiState.value.song.id
            val songAlreadyExistsInPlaylist =
                playlistsWithSongsRepository.isSongAlreadyExistInPlaylist(
                    playlistName = playlistName,
                    songDbId = songDbId
                )
            when (songAlreadyExistsInPlaylist) {
                true -> {
                    _nowPlayingEvents.send(
                        NowPlayingEvents.ShowToast(
                            UiText.StringResource(
                                R.string.song_already_exists_in_playlist,
                                playlistName
                            )
                        )
                    )
                }

                false -> {
                    val savePlaylist = playlistsWithSongsRepository.addSongToPlaylist(
                        playListName = playlistName,
                        songDbId = songDbId
                    )
                    when (savePlaylist) {
                        is Result.Error -> _nowPlayingEvents.send(
                            NowPlayingEvents.ShowToast(
                                savePlaylist.exception
                            )
                        )

                        is Result.Success -> {
                            _nowPlayingEvents.send(
                                NowPlayingEvents.ShowToast(
                                    UiText.StringResource(
                                        R.string.added_to_playlist,
                                        playlistName
                                    )
                                )
                            )
                        }
                    }
                }
            }

            _nowPlayingUiState.update { newState ->
                newState.copy(
                    isSaving = false,
                )
            }
        }
    }

    private fun saveToNewPlaylist() {
        viewModelScope.launch {
            _nowPlayingUiState.update { newState ->
                newState.copy(
                    isSaving = true
                )
            }
            val playlistName = _nowPlayingUiState.value.playListTextFieldValue
            val playlistAlreadyExists =
                playlistsWithSongsRepository.playlistAlreadyExists(playlistName = playlistName)
            when (playlistAlreadyExists) {
                true -> {
                    _nowPlayingEvents.send(NowPlayingEvents.ShowToast(UiText.StringResource(R.string.playlist_already_exist)))
                }

                else -> {
                    val songDbId = _nowPlayingUiState.value.song.id
                    val savePlayList = playlistsWithSongsRepository.createPlaylistWithSongs(
                        playlistName = playlistName,
                        selectedSongIds = listOf(songDbId)
                    )
                    when (savePlayList) {
                        is Result.Error -> {
                            _nowPlayingEvents.send(
                                NowPlayingEvents.ShowToast(
                                    UiText.StringResource(
                                        R.string.playlist_already_exist
                                    )
                                )
                            )
                        }

                        is Result.Success -> {
                            _nowPlayingEvents.send(
                                NowPlayingEvents.ShowToast(
                                    UiText.StringResource(
                                        R.string.playlist_successfully_saved
                                    )
                                )
                            )
                            _nowPlayingUiState.update { newState ->
                                newState.copy(
                                    showCreatePlaylistBottomSheet = false,
                                    playListTextFieldValue = "",
                                )
                            }
                        }
                    }
                }
            }
            _nowPlayingUiState.update { newState ->
                newState.copy(
                    isSaving = false,
                )
            }
        }
    }

    private fun updatePlaylistTextField(newValue: String) {
        if (newValue.length <= 40) {
            _nowPlayingUiState.update { newState ->
                newState.copy(
                    playListTextFieldValue = newValue
                )
            }
        }
    }

    private fun showCreatePlaylist() {
        _nowPlayingUiState.update { newState ->
            newState.copy(
                showCreatePlaylistBottomSheet = true
            )
        }
    }

    private fun hideCreatePlaylist() {
        _nowPlayingUiState.update { newState ->
            newState.copy(
                showCreatePlaylistBottomSheet = false
            )
        }
    }

    private fun hideBottomSheet() {
        _nowPlayingUiState.update { newState ->
            newState.copy(
                isBottomSheetVisible = false
            )
        }
    }

    private fun onPlaylistClick() {
        _nowPlayingUiState.update { newState ->
            newState.copy(
                isBottomSheetVisible = true
            )
        }
    }

    private fun onFavouriteClick() {
        viewModelScope.launch {
            val isFavourite = _nowPlayingUiState.value.isFavourite
            val songDbId = _nowPlayingUiState.value.song.id
            var result: Result<Unit>?
            if (isFavourite) {
                val removeResult = playlistsWithSongsRepository.removeSongFromPlaylist(
                    playlistName = FAVOURITE,
                    songDbId = songDbId
                )
                result = removeResult
            } else {
                val addResult = playlistsWithSongsRepository.addSongToPlaylist(
                    playListName = FAVOURITE,
                    songDbId = songDbId
                )
                result = addResult
            }
            when (result) {
                is Result.Error -> _nowPlayingEvents.send(NowPlayingEvents.ShowToast(message = result.exception))
                is Result.Success -> _nowPlayingEvents.send(
                    NowPlayingEvents.ShowToast(
                        message = UiText.StringResource(
                            R.string.favourite_playlist_updated
                        )
                    )
                )
            }
        }
    }

    private fun seekTo(position: Long) {
        playbackController.seekTo(position = position)
    }

    private fun setInitialSong() {
        viewModelScope.launch {
            when (navKey.nowPlayingData) {
                is NowPlayingData.PlayBySongId -> {
                    val song = async {
                        val songId = navKey.nowPlayingData.songId
                        songRepository.getSongBySongId(songId = songId)
                    }.await()
                    val id = song.id
                    val isFavourite = playlistsWithSongsRepository.isSongInFavourite(songDbId = id)
                    isFavourite.collect { isFavourite ->
                        _nowPlayingUiState.update { newState ->
                            newState.copy(
                                isFavourite = isFavourite,
                                song = song,
                            )
                        }
                    }
                    song.let {
                        playbackController.setMediaItemByIndex(mediaItemsIndex = song.id - 1)
                    }
                }

                NowPlayingData.Play -> playByMediaOrder()

                NowPlayingData.Shuffle -> shuffle()
            }
        }
    }

    private fun setPlaylists() {
        viewModelScope.launch {
            playlistsWithSongsRepository.getPlaylistsWithSongs().collect { playlistsWithSongs ->
                val favourite = playlistsWithSongs.firstOrNull {
                    it.playlist.playlistName == FAVOURITE
                }?.let { (playlist, songs) ->
                    PlayListModel(
                        name = playlist.playlistName,
                        total = songs.size,
                        embeddedArt = songs.firstOrNull()?.embeddedArt
                    )
                }

                val myPlaylists = playlistsWithSongs.filter {
                    it.playlist.playlistName != FAVOURITE
                }.map { (playlist, songs) ->
                    PlayListModel(
                        name = playlist.playlistName,
                        total = songs.size,
                        embeddedArt = songs.firstOrNull()?.embeddedArt
                    )
                }

                _nowPlayingUiState.update { newState ->
                    newState.copy(
                        favouritePlaylistModel = favourite ?: PlayListModel(),
                        playlistList = myPlaylists
                    )
                }
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
                song?.let {
                    playlistsWithSongsRepository.isSongInFavourite(songDbId = song.id)
                        .collect { isFavourite ->
                            _nowPlayingUiState.update { newState ->
                                newState.copy(
                                    mediaPlayerState = mediaPlayerState,
                                    song = song,
                                    isFavourite = isFavourite
                                )
                            }
                        }
                } ?: _nowPlayingUiState.update { newState ->
                    newState.copy(
                        mediaPlayerState = mediaPlayerState,
                        song = Song(),
                        isFavourite = false
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