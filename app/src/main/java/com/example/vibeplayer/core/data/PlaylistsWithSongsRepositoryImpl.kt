package com.example.vibeplayer.core.data

import com.example.vibeplayer.core.database.playlist.PlaylistDao
import com.example.vibeplayer.core.database.playlist.PlaylistEntity
import com.example.vibeplayer.core.database.playlist.toPlaylist
import com.example.vibeplayer.core.database.room_relation.PlaylistsAndSongsDao
import com.example.vibeplayer.core.database.room_relation.PlaylistsAndSongsEntity
import com.example.vibeplayer.core.database.song.toDomainModel
import com.example.vibeplayer.core.domain.PlaylistWithSongsDomain
import com.example.vibeplayer.core.domain.PlaylistsWithSongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber

class PlaylistsWithSongsRepositoryImpl(
    val playlistDao: PlaylistDao,
    val playlistsAndSongsDao: PlaylistsAndSongsDao
) : PlaylistsWithSongsRepository {
    override suspend fun createPlaylistWithSongs(
        playlistName: String,
        selectedSongIds: List<Int>
    ) {
        val playlistId = playlistDao.insertPlaylist(
            playlistEntity = PlaylistEntity(
                playlistId = 0,
                playlistName = playlistName
            )
        ).toInt()

        Timber.tag("MyTag").d("playlistId: $playlistId")

        selectedSongIds.forEach { songRoomId ->
            playlistsAndSongsDao.upsertPlaylistSongCrossRef(
                crossRef = PlaylistsAndSongsEntity(
                    playlistId = playlistId,
                    id = songRoomId
                )
            )
        }
    }

    override fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongsDomain>> {
        return playlistsAndSongsDao.getPlaylistsWithSongs().map { listOfPlaylistWithSongs ->
            listOfPlaylistWithSongs.map { playlistWithSongs ->
                PlaylistWithSongsDomain(
                    songs = playlistWithSongs.songs.toDomainModel(),
                    playlist = playlistWithSongs.playlistEntity.toPlaylist()
                )
            }
        }.flowOn(Dispatchers.IO)
    }

}