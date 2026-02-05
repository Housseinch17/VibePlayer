package com.example.vibeplayer.core.database.playlist

import com.example.vibeplayer.core.domain.Playlist

fun Playlist.toPlaylistEntity(): PlaylistEntity {
    return PlaylistEntity(
        playlistId = playlistId,
        playlistName = playlistName,
        embeddedUri = embeddedUri
    )
}

fun PlaylistEntity.toPlaylist(): Playlist {
    return Playlist(
        playlistId = playlistId,
        playlistName = playlistName,
        embeddedUri = embeddedUri
    )
}