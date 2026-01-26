package com.example.vibeplayer.core.database.song

import com.example.vibeplayer.core.domain.Song

fun List<SongEntity>.toDomainModel(): List<Song> {
    return this.map { entity ->
        Song(
            id = entity.id,
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

fun SongEntity.toSong(): Song {
    return Song(
        id = this.id,
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
        id = this.id,
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