package com.example.vibeplayer.feature.playlist_playback

import com.example.vibeplayer.core.domain.Playlist
import com.example.vibeplayer.core.domain.Song

data class PlaylistPlaybackUiState(
    val playlist: Playlist = Playlist(),
    val songs: List<Song> = emptyList(),
) {
    val playlistState = if (songs.isEmpty()) PlaylistState.EMPTY else PlaylistState.WITH_SONGS
    val totalSongs = songs.size
}

enum class PlaylistState {
    EMPTY,
    WITH_SONGS,
}
