package com.example.vibeplayer.feature.main.presentation

import android.net.Uri
import com.example.vibeplayer.R

data class PlayListModel(
    val name: String = "",
    val id: Int = 0,
    val total: Int = 0,
    val embeddedArt: Uri? = null,
    val errorDrawable: Int = R.drawable.other_playlist,
)
