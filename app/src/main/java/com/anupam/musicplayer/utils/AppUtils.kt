package com.anupam.musicplayer.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette

fun getDominantColor(image: Bitmap): Color {
    val color =  Palette.from(image).generate().getDarkMutedColor(Color.DarkGray.toArgb())
    return Color(color)
}

fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}