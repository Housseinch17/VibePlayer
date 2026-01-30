package com.example.vibeplayer.core.data

import com.example.vibeplayer.R
import com.example.vibeplayer.core.database.playlist.PlaylistDao
import com.example.vibeplayer.core.database.playlist.PlaylistEntity
import com.example.vibeplayer.core.database.room_relation.PlaylistsAndSongsDao
import com.example.vibeplayer.core.database.room_relation.PlaylistsAndSongsEntity
import com.example.vibeplayer.core.database.room_relation.toPlaylistWithSongsDomain
import com.example.vibeplayer.core.domain.PlaylistWithSongsDomain
import com.example.vibeplayer.core.domain.PlaylistsWithSongsRepository
import com.example.vibeplayer.core.domain.Result
import com.example.vibeplayer.core.presentation.ui.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaylistsWithSongsRepositoryImpl(
    val playlistDao: PlaylistDao,
    val playlistsAndSongsDao: PlaylistsAndSongsDao,
    applicationScope: CoroutineScope
) : PlaylistsWithSongsRepository {
    init {
        applicationScope.launch {
            val favouritePlaylistExists = playlistAlreadyExists(playlistName = "Favourite")
            if (!favouritePlaylistExists) {
                saveEmptyPlaylist(playlistName = "Favourite")
            }
        }
    }

    override suspend fun createPlaylistWithSongs(
        playlistName: String,
        selectedSongIds: List<Int>
    ): Result<UiText> {
        val playlistNameAlreadyExists =
            playlistDao.playlistAlreadyExists(playlistName = playlistName)
        return when (playlistNameAlreadyExists) {
            true -> {
                Result.Error(exception = UiText.StringResource(R.string.playlist_already_exist))
            }

            else -> {
                val playlistId = playlistDao.insertPlaylist(
                    playlistEntity = PlaylistEntity(
                        playlistId = 0,
                        playlistName = playlistName
                    )
                ).toInt()

                selectedSongIds.forEach { songRoomId ->
                    playlistsAndSongsDao.upsertPlaylistSongCrossRef(
                        crossRef = PlaylistsAndSongsEntity(
                            playlistId = playlistId,
                            id = songRoomId
                        )
                    )
                }
                Result.Success(UiText.StringResource(R.string.playlist_successfully_saved))
            }
        }
    }

    //we use Dispatchers.IO because we are using mapping
    //This is not Room’s responsibility anymore — that’s your CPU work.
    override fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongsDomain>> {
        return playlistsAndSongsDao.getPlaylistsWithSongs().map { listOfPlaylistWithSongs ->
            listOfPlaylistWithSongs.map { playlistWithSongs ->
                playlistWithSongs.toPlaylistWithSongsDomain()
            }
        }.flowOn(Dispatchers.IO)
    }

    //we use Dispatchers.IO because we are using mapping
    //This is not Room’s responsibility anymore — that’s your CPU work.
    override fun getPlaylistByName(playlistName: String): Flow<PlaylistWithSongsDomain> {
        return playlistsAndSongsDao.getPlaylistByName(playlistName = playlistName)
            .map { playlistWithSongs ->
                playlistWithSongs.toPlaylistWithSongsDomain()
            }.flowOn(Dispatchers.IO)
    }

    //this is only used when the user navigate back before saving any songs to playlist
    //so we have to save playlist only
    override suspend fun saveEmptyPlaylist(playlistName: String) {
        playlistDao.insertPlaylist(
            playlistEntity = PlaylistEntity(
                playlistId = 0,
                playlistName = playlistName
            )
        )
    }

    override suspend fun playlistAlreadyExists(playlistName: String): Boolean {
        return playlistDao.playlistAlreadyExists(playlistName = playlistName)
    }
}