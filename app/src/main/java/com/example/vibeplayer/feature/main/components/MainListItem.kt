package com.example.vibeplayer.feature.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.vibeplayer.core.domain.Song
import com.example.vibeplayer.core.util.toMinutesSeconds
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerImages.SongImageDefault
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerTheme
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.feature.main.dummySongs

@Composable
fun MainListItem(
    song: Song
) {

    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentDescription = null,
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.embeddedArt ?: SongImageDefault)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMediumRegular
            )
        }

        Text(
            text = song.duration.toMinutesSeconds(),
            style = MaterialTheme.typography.bodyMediumRegular,
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun MainListItemPreview() {
    VibePlayerTheme {
        MainListItem(dummySongs[0])
    }
}