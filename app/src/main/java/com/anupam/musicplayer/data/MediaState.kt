package com.anupam.musicplayer.data

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.anupam.musicplayer.ui.theme.BackgroundColor
import java.util.LinkedList
import java.util.Queue

data class MediaState(
    var currentMedia: Int? = null,
    var backgroundColor: Color = BackgroundColor,
    var cover: Bitmap? = null,
    var title: String = "",
    val artist: String = "",
    val mimeType: String = "",
    var isPlaying: Boolean = false,
    var currentPosition: Long = 0L, // * For progress bar slider
    var previousAudio: Uri? = null,
    var nextAudio: Queue<Uri> = LinkedList(),
    var mediaFiles: List<MediaItem> = emptyList(),
    var searchedMediaFiles: List<MediaItem> = emptyList(),
    var favorite: Boolean = false,
    var amplitudes: List<Int> = emptyList() // ? not needed yet
)