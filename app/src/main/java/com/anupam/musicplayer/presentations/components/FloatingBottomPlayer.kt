package com.anupam.musicplayer.presentations.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.viewmodels.MediaEvent

@Composable
fun FloatingBottomPlayer(
    modifier: Modifier = Modifier,
    state: MediaState,
    onEvent: (MediaEvent) -> Unit
) {
    var duration by remember{ mutableLongStateOf(100L) }
    var currentPosition by remember { mutableLongStateOf(40L) }

    LaunchedEffect(key1 = state.currentMedia?.duration) {
        duration = state.currentMedia?.duration ?: 0L
    }

    LaunchedEffect(key1 = state.currentPosition) {
        currentPosition = state.currentPosition
    }
    
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.music_bg),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
        )

        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = state.title,
                color = Color.White
            )
            Text(
                text = state.currentMedia?.artist ?: "Unknowm Artist",
                color = Color.DarkGray
            )
        }

        PlayControl(
            duration = duration,
            currentPosition = currentPosition,
            icon = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            onClick = {
                if (state.isPlaying) {
                    onEvent(MediaEvent.PauseAudio)
                } else {
                    onEvent(MediaEvent.PlayAudio)
                }
            }
        )

    }
}

@Preview
@Composable
private fun FloatingBottomPlayerPreview() {
    FloatingBottomPlayer(
        state = MediaState(
            title = "Music"
        ),
        onEvent = {}
    )
}