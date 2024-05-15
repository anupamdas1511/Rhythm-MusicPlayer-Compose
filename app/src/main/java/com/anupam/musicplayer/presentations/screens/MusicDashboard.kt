package com.anupam.musicplayer.presentations.screens

import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaItem
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.presentations.components.FloatingBottomPlayer
import com.anupam.musicplayer.viewmodels.MediaEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun MusicDashboard(
    mediaState: Flow<MediaState>,
    onEvent: (MediaEvent) -> Unit
) {
    val state = mediaState.collectAsState(initial = MediaState())
    Scaffold (
        bottomBar = {
            // todo: unimplemented component
            FloatingBottomPlayer(state = state.value, onEvent = onEvent)
        }
    ) {
        LazyColumn (
            modifier = Modifier
                .padding(it.calculateTopPadding())
        ) {
            items(state.value.mediaFiles) {mediaItem ->
                MusicItem(
                    audio = mediaItem,
                    onClick = {
                        onEvent(MediaEvent.SelectMedia(mediaItem))
                    }
                )
            }
        }
    }
}

@Composable
fun MusicItem(
    audio: MediaItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onClick.invoke()
            }
    ) {
        Column (
            modifier.weight(10f)
        ) {
            Text(
                text = audio.name,
                color = Color.White,
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
        IconButton(onClick = {
            // todo: unimplemented
        }) {
            Icon(
                painter = painterResource(id = R.drawable.add_to_queue),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}


@Preview
@Composable
private fun MusicItemPreview() {
    MusicItem(
        audio = MediaItem(
            0, "This is a very long text that will be clipped if exceeds the fixed boundary", null, Uri.EMPTY, "Anupam Das", 12345, 0L
        ),
        onClick = {}
    )
}