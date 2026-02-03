package com.example.vibeplayer.core.presentation.designsystem.theme

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

    val SelectedRadioButton: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.radio_button_selected)

    val SelectedRadioButtonTick: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.radio_button_tick)

    val UnSelectedRadioButton: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.radio_button_unselected)

    val Search: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.search)

    val RepeatAll: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.repeat_all)

    val RepeatOne: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.repeat_one)

    val RepeatOff: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.repeat_off)

    val Shuffle: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.shuffle)

    val Clear: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.clear)

    val Add: ImageVector
    @Composable
    get() = ImageVector.vectorResource(R.drawable.add)

    val MenuDots: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.menu_dots)

    val Favourite: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.favourite)

    val FavouriteSelected: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.favourite_selected)

    val Playlist: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.playlist)

    val PlaylistCreate: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.playlist_create)
}