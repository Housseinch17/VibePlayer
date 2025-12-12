package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@Composable
fun rememberRotationEveryTwoSeconds(): Float{
    // Create an infinite transition
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the rotation from 0f to 360f infinitely
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000, // 2 seconds for one full rotation
                easing = LinearEasing    // smooth constant speed
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    return rotation
}