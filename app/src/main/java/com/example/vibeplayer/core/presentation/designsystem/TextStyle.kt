package com.example.vibeplayer.core.presentation.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography.bodyLargeRegular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    )

val Typography.bodyLargeMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    )

val Typography.bodyMediumRegular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )