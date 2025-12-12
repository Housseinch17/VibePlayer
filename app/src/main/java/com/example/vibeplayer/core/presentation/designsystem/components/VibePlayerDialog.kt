package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.vibeplayer.core.presentation.designsystem.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.surfaceBG
import com.example.vibeplayer.core.presentation.designsystem.textSecondary

@Composable
fun VibePlayerDialog(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceBG,
    title: String,
    text: String,
    confirmText: String,
    dismissText: String,
    dismissRequest: () -> Unit = {},
    confirmButtonClick: () -> Unit,
    dismissButtonClick: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        containerColor = containerColor,
        title = {
            Text(
                modifier = Modifier,
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                modifier = Modifier,
                text = text,
                style = MaterialTheme.typography.bodyMediumRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary
                )
            )
        },
        onDismissRequest = dismissRequest,
        dismissButton = {
            VibePlayerButton(
                modifier = Modifier,
                onClick = dismissButtonClick,
                buttonText = dismissText
            )
        },
        confirmButton = {
            VibePlayerButton(
                modifier = Modifier,
                onClick = confirmButtonClick,
                buttonText = confirmText
            )
        }
    )
}