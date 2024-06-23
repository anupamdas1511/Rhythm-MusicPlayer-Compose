package com.anupam.musicplayer.presentations.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.sharp.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaItem

@Composable
fun MusicItem(
    modifier: Modifier = Modifier,
    audio: MediaItem,
    index: Int = 0,
    isPlaying: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            }
            .padding(10.dp)
    ) {
        Column(
            modifier.weight(10f)
        ) {
            Text(
                text = audio.name,
                color = if (isPlaying) Color.Cyan else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            Text(
                text = audio.artist ?: "Unknown Artist",
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
//        Spacer(modifier = Modifier.weight(1f))
        if (audio.favorite) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
        }

        IconButton(onClick = {
            // todo: unimplemented
        }) {
            if (isPlaying) {
                Icon(
                    imageVector = Icons.Sharp.MusicNote,
                    contentDescription = null,
                    tint = Color.Cyan
                )
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = ImageRequest.Builder(context = LocalContext.current)
//                            .data(R.drawable.music_wave)
//                            .crossfade(true)
//                            .build()
//                    ),
//                    contentDescription = null
//                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.add_to_queue),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}