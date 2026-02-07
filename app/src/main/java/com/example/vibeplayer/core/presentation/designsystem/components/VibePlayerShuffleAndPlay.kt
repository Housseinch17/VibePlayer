package com.example.vibeplayer.core.presentation.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.surfaceOutline

@Composable
fun VibePlayerShuffleAndPlay(
    modifier: Modifier = Modifier,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VibePlayerOutlinedButtonWithIcon(
            modifier = Modifier.weight(1f),
            buttonContentIconDescription = stringResource(R.string.shuffle),
            buttonContentIconImageVector = VibePlayerIcons.Shuffle,
            buttonContentText = stringResource(R.string.shuffle),
            borderStroke = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.surfaceOutline
            ),
            onClick = onShuffleClick
        )

        VibePlayerOutlinedButtonWithIcon(
            modifier = Modifier.weight(1f),
            buttonContentIconDescription = stringResource(R.string.play),
            buttonContentIconImageVector = VibePlayerIcons.Play,
            buttonContentText = stringResource(R.string.play),
            borderStroke = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.surfaceOutline
            ),
            onClick = onPlayClick,
        )
    }
}