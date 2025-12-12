package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.core.presentation.designsystem.buttonHover
import com.example.vibeplayer.core.presentation.designsystem.textSecondary

@Composable
fun VibePlayerIconShape(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.buttonHover,
    iconDescription: String,
    iconModifier: Modifier = Modifier
        .padding(10.dp)
        .size(16.dp),
    isClickable: Boolean = true,
    onClick: () -> Unit = {},
    tintColor: Color = MaterialTheme.colorScheme.textSecondary
) {
    Box(
        modifier = modifier
            .background(
                color = containerColor,
                shape = CircleShape
            )
            .clickable(
                enabled = isClickable,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = iconModifier,
            imageVector = imageVector,
            contentDescription = iconDescription,
            tint = tintColor
        )
    }
}