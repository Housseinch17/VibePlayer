package com.example.vibeplayer.core.presentation.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.vibeplayer.R

private val hostGroteskRegular =
    Font(resId = R.font.hostgrotesk_regular, weight = FontWeight.Normal)
private val hostGroteskMedium = Font(resId = R.font.hostgrotesk_medium, weight = FontWeight.Medium)
private val hostGroteskSemiBold =
    Font(resId = R.font.hostgrotesk_semibold, weight = FontWeight.SemiBold)

val hostGroteskFamily = FontFamily(
    fonts = listOf(
        hostGroteskRegular,
        hostGroteskMedium,
        hostGroteskSemiBold
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = hostGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
    ),
)