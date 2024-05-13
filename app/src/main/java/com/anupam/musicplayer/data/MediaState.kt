package com.anupam.musicplayer.data

import android.net.Uri
import java.util.LinkedList
import java.util.Queue

data class MediaState(
    var currentMedia: Uri? = null,
    var title: String = "",
    val mimeType: String = "",
    var isPlaying: Boolean = false,
    var previousAudio: Uri? = null,
    var nextAudio: Queue<Uri> = LinkedList(),
    var mediaFiles: List<MediaItem> = emptyList()
)