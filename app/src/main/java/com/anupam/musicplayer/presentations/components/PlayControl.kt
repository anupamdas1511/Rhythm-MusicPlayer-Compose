package com.anupam.musicplayer.presentations.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaItem
import kotlin.math.PI
import kotlin.time.Duration

@Composable
fun PlayControl(
    modifier: Modifier = Modifier,
    duration: Long = 100,
    currentPosition: Long = 30,
    icon: ImageVector = Icons.Filled.PlayArrow,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)

        Canvas(modifier = modifier) {
            drawCircle(
                color = Color(0xF0949494),
                radius = 50f,
                style = Stroke(
                    width = 5f
                )
            )

            // ? 2 * PI === duration
            // ? angle === currentPosition
            val angle = currentPosition / duration.toFloat() * (360).toFloat()

            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = angle,
                topLeft = center - Offset(50f, 50f),
                size = Size(100f, 100f),
                useCenter = false,
                style = Stroke(
                    width = 5f,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Preview
@Composable
private fun PlayControlPreview() {
    PlayControl(onClick = {})
}