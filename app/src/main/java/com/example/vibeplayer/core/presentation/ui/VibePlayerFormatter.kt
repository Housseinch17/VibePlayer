package com.example.vibeplayer.core.presentation.ui

fun Long.toMinutesSeconds(): String {
    val minutes = this / 1000 / 60
    val seconds = (this / 1000 % 60).toString().padStart(2, '0')
    return "$minutes:$seconds"
}