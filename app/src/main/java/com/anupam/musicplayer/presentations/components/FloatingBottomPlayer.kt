package com.anupam.musicplayer.presentations.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anupam.musicplayer.NavigationItem
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.utils.getDominantColor
import com.anupam.musicplayer.viewmodels.MediaEvent

@Composable
fun FloatingBottomPlayer(
    modifier: Modifier = Modifier,
    state: MediaState,
    onEvent: (MediaEvent) -> Unit,
    navController: NavController? = null
) {
    val context = LocalContext.current
//    val image = remember {
//        mutableStateOf<Bitmap?>(
//            BitmapFactory.decodeResource(
//                context.resources,
//                R.drawable.music_bg
//            )
//        )
//    }
//    var dominantColor by remember { mutableStateOf(Color.White) }
    var duration by remember { mutableLongStateOf(100L) }
    var currentPosition by remember { mutableLongStateOf(40L) }

    if (state.currentMedia != null) {
        LaunchedEffect(key1 = state.mediaFiles[state.currentMedia!!].duration) {
            duration = state.mediaFiles[state.currentMedia!!].duration
        }

//        LaunchedEffect(key1 = state.mediaFiles[state.currentMedia!!].cover) {
//            image.value = state.mediaFiles[state.currentMedia!!].cover
//        }
    }
    LaunchedEffect(key1 = state.currentPosition) {
        currentPosition = state.currentPosition
    }

    val defaultBackground = BitmapFactory.decodeResource(context.resources, R.drawable.music_bg)
//    LaunchedEffect(key1 = image) {
//        dominantColor = getDominantColor(image.value!!)
//    }

    PlayerUI(
        modifier = modifier,
        image = if (state.currentMedia != null) {
            state.cover ?: defaultBackground
        } else defaultBackground,
        dominantColor = state.backgroundColor,
        title = state.title,
        artist = if (state.currentMedia != null) state.mediaFiles[state.currentMedia!!].artist
            ?: "Unknown Artist" else "Unknown Artist",
        isPlaying = state.isPlaying,
        duration = duration,
        currentPosition = currentPosition,
        context = context,
        onEvent = onEvent,
        navController = navController
    )
}

@Composable
fun PlayerUI(
    modifier: Modifier,
    image: Bitmap,
    dominantColor: Color,
    title: String,
    artist: String,
    isPlaying: Boolean,
    duration: Long,
    currentPosition: Long,
    context: Context,
    onEvent: (MediaEvent) -> Unit,
    navController: NavController? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(dominantColor)
            .height(70.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(15f)
                .clickable {
                    // TODO: Implement navigation to music player screen
                    navController?.navigate(NavigationItem.Player.route)
                }
        ) {
            PlayerImageAndInfo(image = image, title = title, artist = artist)
        }

        Spacer(modifier = Modifier.weight(1f))

        PlayerControls(
            isPlaying = isPlaying,
            onPlayPauseClicked = { onEvent(if (isPlaying) MediaEvent.PauseAudio else MediaEvent.PlayAudio) },
            onSkipPreviousClicked = { onEvent(MediaEvent.PreviousAudio(context)) },
            onSkipNextClicked = { onEvent(MediaEvent.NextAudio(context)) },
            duration = duration,
            currentPosition = currentPosition
        )
    }
}

@Composable
fun PlayerImageAndInfo(image: Bitmap, title: String, artist: String) {
    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.clip(RoundedCornerShape(15.dp))
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column {
        Text(text = title, color = Color.White, maxLines = 1)
        Text(text = artist, color = Color(0xF0949494), maxLines = 1)
    }
}

@Composable
fun PlayerControls(
    duration: Long,
    currentPosition: Long,
    isPlaying: Boolean,
    onPlayPauseClicked: () -> Unit,
    onSkipPreviousClicked: () -> Unit,
    onSkipNextClicked: () -> Unit
) {
    IconButton(onClick = onSkipPreviousClicked) {
        Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = null, tint = Color.White)
    }

    PlayControl(
        icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
        onClick = onPlayPauseClicked,
        duration = duration,
        currentPosition = currentPosition
    )

    IconButton(onClick = onSkipNextClicked) {
        Icon(imageVector = Icons.Filled.SkipNext, contentDescription = null, tint = Color.White)
    }
}

@Preview
@Composable
private fun FloatingBottomPlayerPreview() {
    FloatingBottomPlayer(
        state = MediaState(
            title = "This is a very long title for a song"
        ),
        onEvent = {}
    )
}