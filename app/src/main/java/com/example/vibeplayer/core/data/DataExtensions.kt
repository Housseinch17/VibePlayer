package com.example.vibeplayer.core.data

import android.net.Uri
import androidx.media3.common.MediaItem

 fun MediaItem.convertMediaItemToUri(): Uri? {
    val uri = this.localConfiguration?.uri
    return uri
}