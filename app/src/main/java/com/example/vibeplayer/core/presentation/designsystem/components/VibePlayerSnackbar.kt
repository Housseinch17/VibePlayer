package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary

@Composable
fun VibePlayerSnackbar(
    modifier: Modifier = Modifier,
    snackbarData: SnackbarData
) {
    Snackbar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.textSecondary,
        contentColor = MaterialTheme.colorScheme.textPrimary,
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = snackbarData.visuals.message,
                style = MaterialTheme.typography.bodyMediumRegular.copy(
                    color = LocalContentColor.current
                )
            )
        }
    }
}