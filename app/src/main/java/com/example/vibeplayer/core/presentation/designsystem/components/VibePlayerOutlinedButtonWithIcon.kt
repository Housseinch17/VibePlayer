package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary

@Composable
fun VibePlayerOutlinedButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    buttonContentIconImageVector: ImageVector,
    buttonContentIconDescription: String,
    buttonContentText: String,
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.textSecondary),
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.textPrimary
    )
){
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        border = borderStroke,
       colors = buttonColors
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = buttonContentIconImageVector,
                contentDescription = buttonContentIconDescription,
                tint = LocalContentColor.current
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = buttonContentText,
                style = MaterialTheme.typography.bodyLargeMedium.copy(
                    color = LocalContentColor.current
                )
            )
        }
    }
}