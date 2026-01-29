package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary

@Composable
fun VibePlayerOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.textPrimary,
        ),
        border = BorderStroke(
            1.dp, MaterialTheme.colorScheme.surfaceOutline
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLargeMedium
        )
    }
}