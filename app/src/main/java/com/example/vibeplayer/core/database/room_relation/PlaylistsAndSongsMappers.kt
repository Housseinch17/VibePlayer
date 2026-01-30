package com.example.vibeplayer.core.database.room_relation

import com.example.vibeplayer.core.database.playlist.toPlaylist
import com.example.vibeplayer.core.database.playlist.toPlaylistEntity
import com.example.vibeplayer.core.database.song.toDomainModel
import com.example.vibeplayer.core.database.song.toSongEntity
import com.example.vibeplayer.core.domain.PlaylistWithSongsDomain

fun PlaylistWithSongs.toPlaylistWithSongsDomain(): PlaylistWithSongsDomain {
    return PlaylistWithSongsDomain(
        playlist = playlistEntity.toPlaylist(),
        songs = songs?.toDomainModel() ?: emptyList()
    )
}

fun PlaylistWithSongsDomain.toPlaylistWithSongs(): PlaylistWithSongs {
    return PlaylistWithSongs(
        playlistEntity = playlist.toPlaylistEntity(),
        songs = songs.toSongEntity()
    )
}