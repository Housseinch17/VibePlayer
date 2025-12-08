package com.example.vibeplayer.features.vibePlayer.presentation.designsystem.common

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.vibeplayer.core.presentation.designsystem.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.buttonPrimary
import com.example.vibeplayer.core.presentation.designsystem.textPrimary

@Composable
fun VibePlayerButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    buttonText: String
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.buttonPrimary,
            contentColor = MaterialTheme.colorScheme.textPrimary
        )
    ) {
        Text(
            modifier = Modifier,
            text = buttonText,
            style = MaterialTheme.typography.bodyLargeMedium
        )
    }
}