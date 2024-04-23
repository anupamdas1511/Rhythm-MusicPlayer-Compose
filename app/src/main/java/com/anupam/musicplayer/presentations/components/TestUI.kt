package com.anupam.musicplayer.presentations.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.anupam.musicplayer.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TestUI() {
    val context = LocalContext.current
    val imageList = listOf(R.drawable.music_bg, R.drawable.musics)
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var dominantColor by remember { mutableIntStateOf(Color.White.toArgb()) }
    var imageResId = imageList[selectedImageIndex]

    // Load image from resources
    val image = remember { mutableStateOf<ImageBitmap?>(null) }
//    LaunchedEffect(key1 = imageResId) {
//        val inputStream = context.resources.openRawResource(imageResId)
//        image.value = withContext(Dispatchers.IO) {
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            bitmap.asImageBitmap()
//        }
//        inputStream.close()
//    }
//    Palette.from(image.value!!.asAndroidBitmap())
    val inputStream = context.resources.openRawResource(imageResId)
    image.value = BitmapFactory.decodeStream(inputStream).asImageBitmap()
    inputStream.close()
    dominantColor = Palette.from(image.value!!.asAndroidBitmap()).generate().dominantSwatch?.rgb ?: Color.White.toArgb()
//    LaunchedEffect(key1 = selectedImageIndex) {
//        dominantColor = withContext(Dispatchers.IO) {
//            Palette.from(image.value!!.asAndroidBitmap()).generate().dominantSwatch?.rgb ?: Color.White.toArgb()
//        }
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(dominantColor)),
        contentAlignment = Alignment.Center
    ) {
//        Text(text = dominantColor.toString())
        Image(
            bitmap = image.value!!,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun TestUIPreview() {
    TestUI()
}