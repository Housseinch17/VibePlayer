package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun VibePlayerAsyncImage(
    modifier: Modifier = Modifier,
    imageUrl: Any?,
    contentDescription: String,
    errorDrawable: Int,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .error(errorDrawable)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription
    )
}