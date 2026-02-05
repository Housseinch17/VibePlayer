package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.theme.ButtonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.TextDisabled
import com.example.vibeplayer.core.presentation.designsystem.theme.TextPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonDestructive
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textDisabled
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary

@Composable
fun VibePlayerPrimaryButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    isEnabled: Boolean = true,
    showTextOnly: Boolean = true,
    isScanning: Boolean = false,
    buttonContentIconImageVector: ImageVector = VibePlayerIcons.Loader,
    buttonContentIconDescription: String = stringResource(R.string.loader),
    buttonContentText: String = stringResource(R.string.scanning),
    isButtonDestructive: Boolean = false,
    onclick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 11.dp, horizontal = 24.dp),
        onClick = { onclick() },
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isButtonDestructive) MaterialTheme.colorScheme.buttonDestructive else if (!isScanning) MaterialTheme.colorScheme.buttonPrimary else MaterialTheme.colorScheme.buttonHover,
            disabledContainerColor = if (!isScanning) ButtonHover else MaterialTheme.colorScheme.textDisabled,
            contentColor = if (isScanning) MaterialTheme.colorScheme.buttonHover else MaterialTheme.colorScheme.textPrimary
        )
    ) {
        if (showTextOnly) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isEnabled) TextPrimary else TextDisabled
            )
        } else {
            val rotationEveryTwoSeconds = rotationIfScanning(isScanning = isScanning)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationEveryTwoSeconds),
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
}
