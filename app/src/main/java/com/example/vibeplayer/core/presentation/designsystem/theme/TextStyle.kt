package com.example.vibeplayer.core.presentation.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val Typography.bodyLargeRegular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
    )

val Typography.bodyLargeMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
    )

val Typography.bodyMediumRegular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
    )

val Typography.bodySmallRegular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = SurfaceBg,
        textAlign = TextAlign.Center
    )