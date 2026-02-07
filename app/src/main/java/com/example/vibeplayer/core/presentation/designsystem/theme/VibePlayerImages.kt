package com.example.vibeplayer.core.presentation.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.vibeplayer.R

object VibePlayerImages {

    val PlaylistImage: Painter
        @Composable
        get() = painterResource(R.drawable.playlist_img)

    val ScanningRadarImage: Painter
        @Composable
        get() = painterResource(R.drawable.radar_img)
}