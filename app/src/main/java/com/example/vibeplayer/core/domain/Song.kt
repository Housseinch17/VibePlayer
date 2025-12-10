package com.example.vibeplayer.core.domain

data class Song(
    val id: Long = 0,
    val title: String = "",
    val artist: String = "",
    val filePath: String = "",
    val duration: Long = 0,
    val size: Int = 0,
    val embeddedArt: ByteArray? = null,
)
