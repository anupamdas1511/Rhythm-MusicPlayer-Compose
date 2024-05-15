package com.anupam.musicplayer.data

import android.graphics.Bitmap
import android.net.Uri

data class MediaItem(
    val id: Long,
    val name: String,
    val cover: Bitmap?,
    val contentUri: Uri,
    val artist: String?,
    val dateAdded: Long,
    val duration: Long
)