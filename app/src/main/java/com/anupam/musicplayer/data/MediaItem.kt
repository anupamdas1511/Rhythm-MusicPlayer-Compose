package com.anupam.musicplayer.data

import android.net.Uri

data class MediaItem(
    val id: Long,
    val name: String,
    val contentUri: Uri,
    val dateAdded: Long,
    val mimeType: String
)