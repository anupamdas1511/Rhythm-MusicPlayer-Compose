package com.anupam.musicplayer.data

import android.net.Uri

data class AudioItem(
    val id: Long,
    val title: String,
    val contentUri: Uri,
    val dateAdded: Long,
    val mimeType: String
)
