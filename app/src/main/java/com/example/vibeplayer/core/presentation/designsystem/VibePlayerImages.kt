package com.example.vibeplayer.core.presentation.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.vibeplayer.R

object VibePlayerImages {

    val LogoImage: Painter
        @Composable
        get() = painterResource(R.drawable.logo)

    val RadarImage: Painter
        @Composable
        get() = painterResource(R.drawable.radar_img)

    val SongImage: Painter
        @Composable
        get() = painterResource(R.drawable.song_img)

    val SongImageDefault: Painter
        @Composable
        get() = painterResource(R.drawable.song_img_default)
}