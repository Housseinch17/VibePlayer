package com.example.vibeplayer.core.presentation.designsystem.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary
import com.example.vibeplayer.feature.main.presentation.PlayListModel

@SuppressLint("LocalContextResourcesRead")
@Composable
fun VibePlayerPlaylistItem(
    modifier: Modifier = Modifier,
    playListModel: PlayListModel,
    showDots: Boolean = true,
    onMenuDotsClick: (PlayListModel) -> Unit = {},
    onPlaylistClick: ((String) -> Unit)? = null,
    enabled: Boolean = false,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onPlaylistClick?.let {
                        onPlaylistClick(playListModel.name)
                    }
                },
                enabled = enabled
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VibePlayerAsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            imageUrl = playListModel.embeddedArt,
            contentDescription = stringResource(R.string.playlist_image),
            errorDrawable = playListModel.errorDrawable,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                modifier = Modifier,
                text = playListModel.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.textPrimary,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                modifier = Modifier,
                text = if (playListModel.total == 0) "0 ${stringResource(R.string.song)}"
                else context.resources.getQuantityString(
                    R.plurals.song_size,
                    playListModel.total,
                    playListModel.total
                ),
                style = MaterialTheme.typography.bodyMediumRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Start
                )
            )
        }
        if (showDots) {
            Image(
                modifier = Modifier.clickable(onClick = { onMenuDotsClick(playListModel) }),
                imageVector = VibePlayerIcons.MenuDots,
                contentDescription = stringResource(R.string.menu_dots),
            )
        }
    }
}