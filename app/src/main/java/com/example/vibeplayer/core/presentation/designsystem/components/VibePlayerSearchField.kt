package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.buttonHover
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary

@Composable
fun VibePlayerSearchField(
    modifier: Modifier = Modifier,
    searchQuery: String,
    updateSearchQuery: (String) -> Unit,
    onClear: () -> Unit,
){
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth()
            .focusRequester(focusRequester = focusRequester),
        value = searchQuery,
        onValueChange = { newQuery ->
            updateSearchQuery(newQuery)
        },
        shape = RoundedCornerShape(100.dp),
        leadingIcon = {
            Icon(
                modifier = Modifier,
                imageVector = VibePlayerIcons.Search,
                contentDescription = stringResource(R.string.search),
                tint = LocalContentColor.current
            )
        },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(
                    modifier = Modifier,
                    onClick = onClear
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = VibePlayerIcons.Clear,
                        contentDescription = stringResource(R.string.clear),
                        tint = LocalContentColor.current
                    )
                }
            }
        },
        placeholder = {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.bodyLargeRegular.copy(
                    color = LocalContentColor.current
                )
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.buttonHover,
            unfocusedContainerColor = MaterialTheme.colorScheme.buttonHover,
            focusedBorderColor = MaterialTheme.colorScheme.surfaceOutline,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceOutline,
            focusedTextColor = MaterialTheme.colorScheme.textPrimary,
            unfocusedTextColor = MaterialTheme.colorScheme.textPrimary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.textSecondary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.textSecondary,
            focusedTrailingIconColor = MaterialTheme.colorScheme.textSecondary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.textSecondary,
            focusedPlaceholderColor = MaterialTheme.colorScheme.textSecondary,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.textSecondary,
            cursorColor = MaterialTheme.colorScheme.textPrimary
        ),
        singleLine = true
    )
}