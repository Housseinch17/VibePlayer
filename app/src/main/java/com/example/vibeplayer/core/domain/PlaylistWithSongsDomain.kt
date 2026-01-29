package com.example.vibeplayer.core.domain

data class PlaylistWithSongsDomain(
    val playlist: Playlist,
    val songs: List<Song>
)
