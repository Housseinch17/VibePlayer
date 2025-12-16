package com.example.vibeplayer.feature.scan_music.presentation

import com.example.vibeplayer.core.presentation.ui.Constants
import com.example.vibeplayer.core.presentation.ui.Constants.DURATION_DEFAULT
import com.example.vibeplayer.core.presentation.ui.Constants.SIZE_DEFAULT
import com.example.vibeplayer.core.presentation.ui.UiText

data class ScanMusicUi(
    val durationList: List<String> = Constants.durationList,
    val sizeList: List<String> = Constants.sizeList,
    val selectedDuration: String = DURATION_DEFAULT,
    val selectedSize: String = SIZE_DEFAULT,
    val isScanning: Boolean = false,
    val snackbarMessage: UiText? = null
)
