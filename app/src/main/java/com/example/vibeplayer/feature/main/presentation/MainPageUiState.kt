package com.example.vibeplayer.feature.main.presentation

import androidx.annotation.StringRes
import com.example.vibeplayer.R
import com.example.vibeplayer.core.domain.MediaPlayerState
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.ui.UiText

data class MainPageUiState(
    val currentSong: Song = Song(),
    val progressIndicator: Float = 0f,
    val songState: SongState = SongState.Scanning,
    val mediaPlayerState: MediaPlayerState = MediaPlayerState(),
    val selectedMainTabs: MainTabs = MainTabs.SONGS,
    val myPlayList: List<PlayListModel> = emptyList(),
    val favouritePlayList: PlayListModel = PlayListModel(),
    val isBottomSheetVisible: Boolean = false,
    val playListTextField: String = "",
    val snackbarMessage: UiText? = null
) {
    val totalSong = if (songState is SongState.TrackList) songState.songList.size else 0
    val progressIndicatorForLinearProgress: Float =
        if (mediaPlayerState.duration > 0)
            (progressIndicator / mediaPlayerState.duration.toFloat())
                .coerceIn(0f, 1f)
        else 0f

    val isCreateEnabled: Boolean = playListTextField.isNotBlank()
}

sealed interface SongState {
    data object Scanning : SongState
    data object Empty : SongState
    data class TrackList(val songList: List<Song>) : SongState
}

enum class MainTabs(@StringRes val resourceId: Int) {
    SONGS(R.string.songs),
    PLAYLIST(R.string.playlist)
}