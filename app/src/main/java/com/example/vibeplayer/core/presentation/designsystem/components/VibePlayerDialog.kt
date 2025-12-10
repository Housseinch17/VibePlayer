package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceBG
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary

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
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.textPrimary
                )
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
            Button(
                modifier = Modifier,
                onClick = dismissButtonClick
            ) {
                Text(
                    modifier = Modifier,
                    text = dismissText,
                    style = MaterialTheme.typography.bodyLargeMedium.copy(
                        color = MaterialTheme.colorScheme.textSecondary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier,
                onClick = confirmButtonClick
            ) {
                Text(
                    modifier = Modifier,
                    text = confirmText,
                    style = MaterialTheme.typography.bodyLargeMedium.copy(
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                )
            }
        }
    )
}