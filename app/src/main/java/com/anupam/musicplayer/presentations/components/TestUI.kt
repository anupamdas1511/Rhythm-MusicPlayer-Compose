package com.anupam.musicplayer.presentations.components

//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
//import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.drawWithContent
//import androidx.compose.ui.geometry.CornerRadius
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Paint
//import androidx.compose.ui.graphics.PaintingStyle
//import androidx.compose.ui.graphics.drawscope.ContentDrawScope
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import com.anupam.musicplayer.R
//
//@Composable
//fun TestUI() {
//
//}
//
//fun ContentDrawScope.drawNeonStroke(
//    radius: Dp,
//    color: Color
//) {
//    val paint = Paint().apply {
//        style = PaintingStyle.Stroke
//        strokeWidth = radius.toPx() / 5 // Adjust the stroke width relative to the radius
//    }
//
//    val frameworkPaint = paint.asFrameworkPaint()
////    val color = Color.Magenta
//
//    this.drawIntoCanvas {
//        frameworkPaint.color = color.copy(alpha = 0f).toArgb()
//        frameworkPaint.setShadowLayer(
//            radius.toPx() / 2, 0f, 0f, color.copy(alpha = 0.5f).toArgb()
//        )
//        it.drawRoundRect(
//            left = 0f,
//            top = 0f,
//            right = size.width,
//            bottom = size.height,
//            radiusX = radius.toPx(),
//            radiusY = radius.toPx(),
//            paint = paint
//        )
//    }
//
////    drawRoundRect(
////        color = Color.Magenta,
////        size = size,
////        cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
////        style = Stroke(width = 1.dp.toPx())
////    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview
//@Composable
//private fun TestUIPreview() {
//    CompositionLocalProvider(
//        LocalMinimumInteractiveComponentEnforcement provides false,
//    ) {
////        OutlinedButton(
////            onClick = { },
////            modifier = Modifier.drawWithContent {
////                drawContent()
////                drawNeonStroke(radius = 8.dp)
////            },
////            border = BorderStroke(1.dp, Color.Transparent),
////        ) {
////            Text(text = "OUTLINED BUTTON", color = Color.Magenta)
////        }
////
////        Spacer(modifier = Modifier.width(20.dp))
//
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .size(500.dp)
//                .background(Color.White)
//        ) {
//            Box (
//                modifier = Modifier
//                    .drawWithContent {
//                        drawNeonStroke(radius = 700.dp, color = Color.Black)
//                        drawContent()
//                    }
//                    .background(Color.Black)
//                    .size(300.dp)
//            ) {
//                Image(painter = painterResource(id = R.drawable.music_bg), contentDescription = null)
//            }
//        }
//    }
//}