package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.core.presentation.designsystem.theme.ButtonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.ShadowColor
import com.example.vibeplayer.core.presentation.designsystem.theme.TextDisabled
import com.example.vibeplayer.core.presentation.designsystem.theme.TextPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerTheme

@Composable
fun VibePlayerPrimaryButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    isEnabled: Boolean = true,
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
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = ButtonHover,
        )

    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isEnabled) TextPrimary else TextDisabled
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VibePlayerPrimaryButtonPreview() {
    VibePlayerTheme {
        VibePlayerPrimaryButton(isEnabled = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun VibePlayerPrimaryButtonDisabledPreview() {
    VibePlayerTheme {
        VibePlayerPrimaryButton(isEnabled = false)
    }
}