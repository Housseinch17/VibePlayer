package com.example.vibeplayer.core.database

import com.example.vibeplayer.core.domain.Song

fun List<SongEntity>.toDomainModel(): List<Song> {
    return this.map { entity ->
        Song(
            id = entity.id,
            title = entity.title,
            artist = entity.artist,
            filePath = entity.filePath,
            duration = entity.duration,
            size = entity.size,
            embeddedArt = entity.embeddedArt
        )
    }
}

fun Song.toEntity(): SongEntity {
    return SongEntity(
        id = this.id,
        title = this.title,
        artist = this.artist,
        filePath = this.filePath,
        duration = this.duration,
        size = this.size,
        embeddedArt = this.embeddedArt
    )
}