package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.theme.ButtonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.ShadowColor
import com.example.vibeplayer.core.presentation.designsystem.theme.TextDisabled
import com.example.vibeplayer.core.presentation.designsystem.theme.TextPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.textDisabled

@Composable
fun VibePlayerPrimaryButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    isEnabled: Boolean = true,
    isScanning: Boolean = false,
    onclick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val showShadow = isEnabled && !isPressed

    Button(
        modifier = modifier
            .then(
                if (showShadow) {
                    Modifier.dropShadow(
                        shape = RoundedCornerShape(100.dp),
                        shadow = Shadow(
                            radius = 8.dp,
                            color = ShadowColor,
                            spread = 2.dp,
                            offset = DpOffset(y = 2.dp, x = 0.dp),
                        )
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(100.dp),
        contentPadding = PaddingValues(vertical = 11.dp, horizontal = 24.dp),
        onClick = { onclick() },
        enabled = isEnabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (!isScanning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.buttonHover,
            disabledContainerColor = if (!isScanning) ButtonHover else MaterialTheme.colorScheme.textDisabled,
        )

    ) {
        if (!isScanning) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isEnabled) TextPrimary else TextDisabled
            )
        } else {
            val rotationEveryTwoSeconds = rotationIfScanning(isScanning = true)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.rotate(rotationEveryTwoSeconds),
                    imageVector = VibePlayerIcons.Loader,
                    contentDescription = stringResource(R.string.loader),
                    tint = LocalContentColor.current
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    text = stringResource(R.string.scanning),
                    style = MaterialTheme.typography.bodyLargeMedium.copy(
                        color = LocalContentColor.current
                    )
                )
            }
        }
    }
}
