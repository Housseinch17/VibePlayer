package com.example.vibeplayer.core.domain

import com.example.vibeplayer.core.presentation.ui.UiText

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: UiText) : Result<Nothing>()
}