package com.anupam.musicplayer.presentations.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anupam.musicplayer.R

@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    glowingColor: Color,
    containerColor: Color,
    cornerRadius: Dp = 0.dp,
    glowingRadius: Dp = 20.dp,
    layers: Int = 5,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(500.dp)
            .background(containerColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .drawWithContent {
                    drawNeonStroke(radius = 800.dp, color = glowingColor)
                    drawContent()
                }
                .clip(CircleShape)
                .background(glowingColor)
                .size(250.dp)
        ) {
            content()
        }
    }
}

fun ContentDrawScope.drawNeonStroke(
    radius: Dp,
    color: Color
) {
    val paint = Paint().apply {
        style = PaintingStyle.Stroke
        strokeWidth = radius.toPx() / 5 // Adjust the stroke width relative to the radius
    }

    val frameworkPaint = paint.asFrameworkPaint()
//    val color = Color.Magenta

    this.drawIntoCanvas {
        frameworkPaint.color = color.copy(alpha = 0f).toArgb()
        frameworkPaint.setShadowLayer(
            radius.toPx() / 2, 0f, 0f, color.copy(alpha = 0.5f).toArgb()
        )
        it.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = radius.toPx(),
            radiusY = radius.toPx(),
            paint = paint
        )
    }
}

@Preview
@Composable
private fun GlowingCardPreview() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(500.dp)
    ) {
        GlowingCard(
            modifier = Modifier.size(200.dp),
            glowingColor = Color.Black,
            containerColor = Color.Transparent,
            layers = 20
        ) {
            Image(painter = painterResource(id = R.drawable.music_bg), contentDescription = null)
        }
    }
}