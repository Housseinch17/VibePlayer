package com.example.vibeplayer.core.database.song

import com.example.vibeplayer.core.domain.Song

fun List<SongEntity>.toDomainModel(): List<Song> {
    return this.map { entity ->
        Song(
            id = entity.songDbId,
            songId = entity.songId,
            title = entity.title,
            artist = entity.artist,
            filePath = entity.filePath,
            duration = entity.duration,
            size = entity.size,
            embeddedArt = entity.embeddedArt,
            audioUri = entity.audioUri
        )
    }
}

fun List<Song>.toSongEntity(): List<SongEntity>{
    return this.map { song ->
        SongEntity(
            songDbId = song.id,
            songId = song.songId,
            title = song.title,
            artist = song.artist,
            filePath = song.filePath,
            duration = song.duration,
            size = song.size,
            embeddedArt = song.embeddedArt,
            audioUri = song.audioUri
        )
    }
}
fun SongEntity.toSong(): Song {
    return Song(
        id = this.songDbId,
        songId = this.songId,
        title = this.title,
        artist = this.artist,
        filePath = this.filePath,
        duration = this.duration,
        size = this.size,
        embeddedArt = this.embeddedArt,
        audioUri = this.audioUri
    )
}

fun Song.toEntity(): SongEntity {
    return SongEntity(
        songDbId = this.id,
        songId = this.songId,
        title = this.title,
        artist = this.artist,
        filePath = this.filePath,
        duration = this.duration,
        size = this.size,
        embeddedArt = this.embeddedArt,
        audioUri = this.audioUri
    )
}