package com.anupam.musicplayer.presentations.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 4.dp,
    thumbRadius: Dp = 10.dp,
    activeTrackColor: Color = Color.Green,
    inactiveTrackColor: Color = Color.Gray,
    thumbColor: Color = Color.Blue
) {
    val thumbRadiusPx = with(LocalDensity.current) { thumbRadius.toPx() }
    val trackHeightPx = with(LocalDensity.current) { trackHeight.toPx() }

    Box(
        modifier = modifier
            .height(thumbRadius * 2)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val newValue = change.position.x / size.width
                    onValueChange(newValue.coerceIn(0f, 1f))
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Draw inactive track
            val trackStart = Offset(0f, center.y)
            val trackEnd = Offset(size.width, center.y)
            drawLine(
                color = inactiveTrackColor,
                start = trackStart,
                end = trackEnd,
                strokeWidth = trackHeightPx
            )
            // Draw active track
            drawLine(
                color = activeTrackColor,
                start = trackStart,
                end = Offset(size.width * value, center.y),
                strokeWidth = trackHeightPx
            )
            // Draw thumb with neon stroke
            val thumbCenter = Offset(size.width * value, center.y)
            drawCircle(
                color = thumbColor,
                radius = thumbRadiusPx,
                center = thumbCenter
            )
        }
    }
}


@Preview
@Composable
private fun CustomSliderPreview() {
//    CustomSlider(value = 0.5f, onValueChange = {})
    Slider(
        value = 0.3f,
        onValueChange = {},
        colors = SliderDefaults.colors(

        ),
        modifier = Modifier
            .drawWithContent {
                drawNeonStroke(radius = 10.dp, color = Color.Blue)
                drawContent()
            }
    )
}