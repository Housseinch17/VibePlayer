@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceBG
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import kotlinx.coroutines.launch

@Composable
fun VibePlayerBottomSheet(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    text: String = stringResource(R.string.create_new_playlist),
    buttonText: String = stringResource(R.string.create),
    isButtonEnabled: Boolean,
    onCancelClick: () -> Unit,
    onButtonClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = onCancelClick,
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceBG
                )
                .blur(12.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = MaterialTheme.colorScheme.surfaceBG.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.surfaceBG.copy(alpha = 0.3f)
                )
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                modifier = Modifier,
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.textPrimary
                )
            )
            CreatePlayListTextField(
                value = value,
                onValueChange = onValueChange
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                VibePlayerOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.cancel),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onCancelClick()
                        }
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                VibePlayerPrimaryButton(
                    modifier = Modifier.weight(1f),
                    isEnabled = isButtonEnabled,
                    onclick = onButtonClick,
                    text = buttonText
                )
            }
        }
    }
}

@Composable
fun CreatePlayListTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        shape = RoundedCornerShape(100.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.buttonHover,
            unfocusedContainerColor = MaterialTheme.colorScheme.buttonHover,
            focusedBorderColor = MaterialTheme.colorScheme.surfaceOutline,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceOutline,
            cursorColor = MaterialTheme.colorScheme.textSecondary
        ),
        singleLine = true,
        placeholder = {
            Text(
                text = stringResource(R.string.enter_playlist_name),
                style = MaterialTheme.typography.bodyLargeRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Start
                )
            )
        },
        trailingIcon = {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "${value.length}/40",
                style = MaterialTheme.typography.bodyLargeRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Start
                )
            )
        }
    )
}