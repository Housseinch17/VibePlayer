package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonPrimary30
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary

@Composable
fun VibePlayerTextWithTwoRadioButtons(
    modifier: Modifier = Modifier,
    text: String,
    firstRadioText: String,
    secondRadioText: String,
    abbreviation: String,
    selectedRadio: String,
    isEnabled: Boolean,
    selectRadio: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            style = MaterialTheme.typography.bodyLargeMedium,
        )
        VibePlayerTwoRadioButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            firstRadioText = firstRadioText,
            secondRadioText = secondRadioText,
            selectedRadioText = selectedRadio,
            abbreviation = abbreviation,
            isEnabled = isEnabled,
            selectRadio = selectRadio
        )
    }
}

@Composable
fun VibePlayerTwoRadioButton(
    modifier: Modifier = Modifier,
    firstRadioText: String,
    secondRadioText: String,
    selectedRadioText: String,
    abbreviation: String,
    isEnabled: Boolean,
    selectRadio: (String) -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        VibePlayerRadioButton(
            modifier = Modifier.weight(1f),
            radioText = firstRadioText,
            selectedText = selectedRadioText,
            abbreviation = abbreviation,
            isEnabled = isEnabled,
            selectRadio = selectRadio
        )

        VibePlayerRadioButton(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            radioText = secondRadioText,
            selectedText = selectedRadioText,
            abbreviation = abbreviation,
            isEnabled = isEnabled,
            selectRadio = selectRadio
        )
    }
}

@Composable
fun VibePlayerRadioButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    radioText: String,
    selectedText: String,
    abbreviation: String,
    selectRadio: (String) -> Unit,
) {
    val isSelected = radioText == selectedText
    Box(
        modifier = modifier.border(
            border = BorderStroke(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.buttonPrimary30 else MaterialTheme.colorScheme.textSecondary
            ),
            shape = RoundedCornerShape(100.dp)
        ),
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                modifier = Modifier,
                enabled = isEnabled,
                onClick = {
                    selectRadio(radioText)
                }
            ) {
                Icon(
                    modifier = Modifier,
                    imageVector = if (isSelected) VibePlayerIcons.SelectedRadioButton else VibePlayerIcons.UnSelectedRadioButton,
                    contentDescription = stringResource(R.string.radio_button),
                    tint = Color.Unspecified
                )
            }

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                text = radioText + abbreviation,
                style = MaterialTheme.typography.bodyLargeMedium.copy(
                    color = MaterialTheme.colorScheme.textPrimary
                )
            )
        }
    }
}