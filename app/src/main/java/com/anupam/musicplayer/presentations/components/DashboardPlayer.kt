package com.anupam.musicplayer.presentations.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.sharp.Pause
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.anupam.musicplayer.NavigationItem
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.viewmodels.MediaEvent

@Composable
fun DashboardPlayer(
    modifier: Modifier = Modifier,
    state: MediaState,
    onEvent: (MediaEvent) -> Unit,
    navController: NavController,
    isTabletMode: Boolean = false
) {
    val context = LocalContext.current
    var duration by remember { mutableLongStateOf(100L) }
    var currentPosition by remember { mutableLongStateOf(40L) }

    if (state.currentMedia != null) {
        LaunchedEffect(key1 = state.mediaFiles[state.currentMedia!!].duration) {
            duration = state.mediaFiles[state.currentMedia!!].duration
        }
    }
    LaunchedEffect(key1 = state.currentPosition) {
        currentPosition = state.currentPosition
    }
    val defaultBackground = BitmapFactory.decodeResource(context.resources, R.drawable.music_bg)
    val image = if (state.currentMedia != null) {
        state.cover ?: defaultBackground
    } else defaultBackground
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = modifier
                .padding(horizontal = 30.dp)
                .size(200.dp)
                .drawWithContent {
                    drawNeonStroke(radius = 200.dp, color = state.backgroundColor)
                    drawContent()
                }
                .clickable {
                    if (!isTabletMode)
                        navController.navigate(NavigationItem.Player.route)
                }
        ) {
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
            )

            Canvas(modifier = modifier.size(200.dp)) {
                drawCircle(
                    color = Color(0xF0949494),
                    radius = size.height / 2,
                    style = Stroke(
                        width = 10f
                    )
                )

                // ? 2 * PI === duration
                // ? angle === currentPosition
                val angle = currentPosition / duration.toFloat() * (360).toFloat()

                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = angle,
                    topLeft = center - Offset(size.width / 2, size.height / 2),
                    size = Size(size.width, size.height),
                    useCenter = false,
                    style = Stroke(
                        width = 12f,
                        cap = StrokeCap.Round
                    )
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        DashboardPlayerControls(state = state, onEvent = onEvent)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun DashboardPlayerControls(
    modifier: Modifier = Modifier,
    state: MediaState,
    onEvent: (MediaEvent) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.weight(2f))
        val playerButtonSize = 40.dp
        Button(
            onClick = {
                onEvent(if (state.isPlaying) MediaEvent.PauseAudio else MediaEvent.PlayAudio)
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue
            ),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .size(playerButtonSize * 1.4f)
                .drawWithContent {
                    drawNeonStroke(radius = 100.dp, color = Color.Blue)
                    drawContent()
                }
        ) {
            Image(
                imageVector = if (state.isPlaying) Icons.Sharp.Pause else Icons.Sharp.PlayArrow,
                contentDescription = "Play",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(playerButtonSize)
                    .semantics { role = Role.Button }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun DashboardPlayerPreview() {
    DashboardPlayer(
        state = MediaState(
            title = "This is a very long title for a song"
        ),
        onEvent = {},
        navController = rememberNavController()
    )
}