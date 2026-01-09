package com.example.vibeplayer.core.presentation.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val colorScheme = darkColorScheme(
    primary = ButtonPrimary,
    background = SurfaceBg
)

@Composable
fun VibePlayerTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = RoundedShapes,
        content = content
    )
}