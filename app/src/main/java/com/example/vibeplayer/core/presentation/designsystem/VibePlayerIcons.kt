package com.example.vibeplayer.core.presentation.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.vibeplayer.R

object VibePlayerIcons {

    val Logo: ImageVector
    @Composable
    get() = ImageVector.vectorResource(R.drawable.logo_icon)

    val Scan: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.scan)

    val Play: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.play)

    val Pause: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.pause)

    val SkipNext: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.skip_next)

    val SkipPrevious: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.skip_previous)

    val ArrowLeft: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.arrow_left)

    val ArrowUp: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.arrow_up)

    val ChevronDown: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.chevron_down)

    val Loader: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.loader)

    val Fab: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.fab)

    val SelectedRadioButton: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.radio_button_selected)

    val UnSelectedRadioButton: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.radio_button_unselected)

}