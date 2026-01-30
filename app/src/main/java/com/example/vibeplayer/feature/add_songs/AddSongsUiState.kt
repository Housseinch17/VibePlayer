package com.example.vibeplayer.feature.add_songs

import com.example.vibeplayer.R
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.presentation.ui.UiText

data class AddSongsUiState(
    val playlistName: String = "",
    val isLoading: Boolean = false,
    val songsList: List<Song> = emptyList(),
    val searchValue: String = "",
    val filteredSongs: List<Song> = emptyList(),
    val selectedIds: List<Int> = emptyList(),
    val isSaving: Boolean = false,
    val snackbarMessage: UiText? = null
) {
    val title: UiText = if (selectedIds.isNotEmpty()) {
        UiText.StringResource(R.string.selected_ids, selectedIds.size)
    } else {
        UiText.StringResource(R.string.add_songs)
    }
    val selectedAll: Boolean = selectedIds.size == songsList.size && songsList.isNotEmpty()
    val canSaveToDb: Boolean = selectedIds.isNotEmpty() && !isSaving
}
