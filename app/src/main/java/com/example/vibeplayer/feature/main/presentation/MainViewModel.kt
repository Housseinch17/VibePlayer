package com.example.vibeplayer.feature.main.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibeplayer.R
import com.example.vibeplayer.app.domain.NowPlayingData
import com.example.vibeplayer.core.data.Constants.FAVOURITE
import com.example.vibeplayer.core.domain.PlaybackController
import com.example.vibeplayer.core.domain.PlaylistsWithSongsRepository
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.domain.SongRepository
import com.example.vibeplayer.core.presentation.ui.UiText
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

sealed interface MainPageEvents {
    data object NavigateToScanMusic : MainPageEvents
    data class NavigateToNowPlaying(val nowPlayingData: NowPlayingData) : MainPageEvents
    data object NavigateToSearch : MainPageEvents
    data class NavigateToAddSongs(val playlistName: String) : MainPageEvents
    data class ShowToast(val message: UiText) : MainPageEvents
    data class NavigateToPlaylist(val playlistId: Int): MainPageEvents
}

sealed interface MainPageActions {
    data object ScanAgain : MainPageActions
    data object NavigateToScanMusic : MainPageActions
    data class NavigateToNowPlaying(val nowPlayingData: NowPlayingData) : MainPageActions
    data object NavigateToSearch : MainPageActions
    data object NavigateAndPlay : MainPageActions
    data object NavigateAndShuffle : MainPageActions
    data object NavigateToAddSongs : MainPageActions
    data object SetCurrentSong : MainPageActions
    data object Play : MainPageActions
    data object Pause : MainPageActions
    data object PlayNext : MainPageActions
    data object PlayPrevious : MainPageActions
    data class UpdateMainTabs(val mainTabs: MainTabs) : MainPageActions
    data object ShowCreatePlaylistBottomSheet : MainPageActions
    data object HideBottomSheet : MainPageActions
    data class UpdatePlaylistTextField(val value: String) : MainPageActions
    data class OnPlaylist(val playlistId: Int) : MainPageActions
    data object ShowRenameBottomSheet : MainPageActions
    data object HideRenameBottomSheet : MainPageActions
    data object OnRename : MainPageActions
    data class OnChangeCover(val imageUri: Uri) : MainPageActions
    data object OnDelete : MainPageActions
    data class OnMenuDots(val playListModel: PlayListModel) : MainPageActions
    data object OnMenuDotsDismiss : MainPageActions
    data class UpdateRenameTextField(val newValue: String) : MainPageActions
    data object ShowDeleteBottomSheet : MainPageActions
    data object HideDeleteBottomSheet : MainPageActions
    data object OnDeletePlaylistBottomSheet : MainPageActions
}

class MainViewModel(
    private val songRepository: SongRepository,
    private val playbackController: PlaybackController,
    private val playlistsWithSongsRepository: PlaylistsWithSongsRepository,
) : ViewModel() {
    private val _mainPageUiState = MutableStateFlow(MainPageUiState())
    val mainPageUiState = _mainPageUiState.onStart {
        viewModelScope.launch {
            initialSetup()
            setPlayerState()
            setProgressIndicator()
            setPlayLists()
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

            is MainPageActions.UpdatePlaylistTextField -> updatePlaylistTextField(newValue = mainPageActions.value)

            MainPageActions.NavigateAndPlay -> navigateAndPlay()
            MainPageActions.NavigateAndShuffle -> navigateAndShuffle()
            MainPageActions.SetCurrentSong -> setCurrentSongPlaying()
            MainPageActions.Pause -> pause()
            MainPageActions.Play -> play()
            MainPageActions.PlayNext -> playNext()
            MainPageActions.PlayPrevious -> playPrevious()
            MainPageActions.NavigateToAddSongs -> navigateToAddSongs()
            MainPageActions.ShowCreatePlaylistBottomSheet -> showBottomSheet()
            MainPageActions.HideBottomSheet -> hideBottomSheet()
            is MainPageActions.OnChangeCover -> changeCoverPlaylist(embeddedUri = mainPageActions.imageUri)
            MainPageActions.OnDelete -> showDeleteBottomSheet()
            is MainPageActions.OnPlaylist -> playPlaylist(playlistId = mainPageActions.playlistId)
            MainPageActions.OnRename -> renamePlaylist()
            is MainPageActions.OnMenuDots -> onMenuDotsClick(playListModel = mainPageActions.playListModel)
            MainPageActions.OnMenuDotsDismiss -> onMenuDotsDismiss()
            MainPageActions.HideRenameBottomSheet -> hideRenameBottomSheet()
            MainPageActions.ShowRenameBottomSheet -> showRenameBottomSheet()
            is MainPageActions.UpdateRenameTextField -> updateRenameTextField(newValue = mainPageActions.newValue)
            MainPageActions.HideDeleteBottomSheet -> hideDeleteBottomSheet()
            MainPageActions.ShowDeleteBottomSheet -> showDeleteBottomSheet()
            MainPageActions.OnDeletePlaylistBottomSheet -> deletePlaylist()
        }
    }

    private fun showDeleteBottomSheet() {
        _mainPageUiState.update { newState ->
            newState.copy(
                showDeleteBottomSheet = true
            )
        }
    }

    private fun hideDeleteBottomSheet() {
        _mainPageUiState.update { newState ->
            newState.copy(
                showDeleteBottomSheet = false
            )
        }
    }

    private fun updateRenameTextField(newValue: String) {
        _mainPageUiState.update { newState ->
            newState.copy(
                renameTextField = newValue
            )
        }
    }

    private fun onMenuDotsClick(playListModel: PlayListModel) {
        _mainPageUiState.update { newState ->
            newState.copy(
                showCurrentPlaylistBottomSheet = true,
                currentPlaylist = playListModel
            )
        }
    }


    private fun onMenuDotsDismiss() {
        _mainPageUiState.update { newState ->
            newState.copy(
                showCurrentPlaylistBottomSheet = false,
                currentPlaylist = PlayListModel()
            )
        }
    }

    private fun playPlaylist(playlistId: Int) {
        viewModelScope.launch {
            _mainPageEvents.send(MainPageEvents.NavigateToPlaylist(playlistId = playlistId))
        }
    }

    private fun renamePlaylist() {
        viewModelScope.launch {
            val currentPlaylistName = _mainPageUiState.value.currentPlaylist.name
            val newPlaylistName = _mainPageUiState.value.renameTextField
            val renameResult = playlistsWithSongsRepository.renamePlaylistName(
                playlistName = currentPlaylistName,
                newPlaylistName = newPlaylistName
            )
            when (renameResult) {
                is Result.Error -> {
                    _mainPageEvents.send(MainPageEvents.ShowToast(message = renameResult.exception))
                }

                is Result.Success -> {
                    _mainPageEvents.send(
                        MainPageEvents.ShowToast(
                            message = UiText.StringResource(
                                R.string.successfully_renamed
                            )
                        )
                    )
                    //close bottom sheet and reset currentPlaylist
                    onMenuDotsDismiss()
                    _mainPageUiState.update { newState ->
                        newState.copy(
                            renameTextField = "",
                            showRenameBottomSheet = false,
                        )
                    }
                }
            }
        }
    }

    private fun changeCoverPlaylist(embeddedUri: Uri) {
        viewModelScope.launch {
            val currentPlaylistName = _mainPageUiState.value.currentPlaylist.name
            val changeCoverResult = playlistsWithSongsRepository.changeCover(
                playlistName = currentPlaylistName,
                embeddedUri = embeddedUri
            )
            when (changeCoverResult) {
                is Result.Error -> {
                    _mainPageEvents.send(MainPageEvents.ShowToast(message = changeCoverResult.exception))
                }

                is Result.Success -> {
                    //close bottom sheet and reset currentPlaylist
                    onMenuDotsDismiss()
                    _mainPageEvents.send(
                        MainPageEvents.ShowToast(
                            message = UiText.StringResource(
                                R.string.successfully_changed_cover
                            )
                        )
                    )
                }
            }
        }
    }

    private fun deletePlaylist() {
        viewModelScope.launch {
            val currentPlaylistName = _mainPageUiState.value.currentPlaylist.name
            val deleteResult =
                playlistsWithSongsRepository.deletePlaylist(playlistName = currentPlaylistName)
            when (deleteResult) {
                is Result.Error -> {
                    _mainPageEvents.send(MainPageEvents.ShowToast(message = deleteResult.exception))
                }

                is Result.Success -> {
                    hideDeleteBottomSheet()
                    //close bottom sheet and reset currentPlaylist
                    onMenuDotsDismiss()
                    _mainPageEvents.send(
                        MainPageEvents.ShowToast(
                            message = UiText.StringResource(
                                R.string.successfully_deleted
                            )
                        )
                    )
                }
            }
        }
    }

    private fun showRenameBottomSheet() {
        _mainPageUiState.update { newState ->
            newState.copy(
                showRenameBottomSheet = true
            )
        }
    }

    private fun hideRenameBottomSheet() {
        _mainPageUiState.update { newState ->
            newState.copy(
                showRenameBottomSheet = false
            )
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

    private fun setPlayLists() {
        viewModelScope.launch {
            playlistsWithSongsRepository.getPlaylistsWithSongs().collect { playlistsWithSongs ->
                val favourite = playlistsWithSongs.firstOrNull {
                    it.playlist.playlistName == FAVOURITE
                }?.let { (playlist, songs) ->
                    PlayListModel(
                        name = playlist.playlistName,
                        id = playlist.playlistId,
                        total = songs.size,
                        embeddedArt = playlist.embeddedUri ?: songs.firstOrNull()?.embeddedArt,
                        errorDrawable = R.drawable.favourite_playlist
                    )
                }

                val myPlaylists = playlistsWithSongs.filter {
                    it.playlist.playlistName != FAVOURITE
                }.map { (playlist, songs) ->
                    PlayListModel(
                        name = playlist.playlistName,
                        id = playlist.playlistId,
                        total = songs.size,
                        embeddedArt = playlist.embeddedUri ?: songs.firstOrNull()?.embeddedArt
                    )
                }

                _mainPageUiState.update { newState ->
                    newState.copy(
                        favouritePlayList = favourite ?: PlayListModel(),
                        myPlayList = myPlaylists

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

    private fun navigateToAddSongs() {
        viewModelScope.launch {
            val playlistName = _mainPageUiState.value.playListTextField
            val playlistAlreadyExists =
                playlistsWithSongsRepository.playlistAlreadyExists(playlistName = playlistName)
            when (playlistAlreadyExists) {
                true -> {
                    showSnackbarMessage(UiText.StringResource(R.string.playlist_already_exist))
                }

                else -> {
                    _mainPageEvents.send(MainPageEvents.NavigateToAddSongs(playlistName = playlistName))
                }
            }
            _mainPageUiState.update { newState ->
                newState.copy(
                    playListTextField = "",
                    isShowCreatePlaylistBottomSheetVisible = false
                )
            }
        }
    }

    private fun showSnackbarMessage(snackbarMessage: UiText?) {
        viewModelScope.launch {
            _mainPageUiState.update { newState ->
                newState.copy(
                    snackbarMessage = snackbarMessage
                )
            }
            delay(1.seconds)
            _mainPageUiState.update { newState ->
                newState.copy(
                    snackbarMessage = null
                )
            }
        }
    }

    private fun showBottomSheet() {
        _mainPageUiState.update { newState ->
            newState.copy(
                isShowCreatePlaylistBottomSheetVisible = true
            )
        }
    }

    private fun hideBottomSheet() {
        _mainPageUiState.update { newState ->
            newState.copy(
                isShowCreatePlaylistBottomSheetVisible = false,
                playListTextField = ""
            )
        }
    }

    private fun updatePlaylistTextField(newValue: String) {
        if (newValue.length <= 40) {
            _mainPageUiState.update { newState ->
                newState.copy(
                    playListTextField = newValue
                )
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