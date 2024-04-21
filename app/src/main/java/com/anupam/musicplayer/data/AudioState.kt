package com.anupam.musicplayer.data

import android.provider.MediaStore.Audio
import java.util.LinkedList
import java.util.Queue

data class AudioState(
    var currentAudio: Audio,
    var title: String,
    var isPlaying: Boolean,
    var previousAudio: Audio? = null,
    var nextAudio: Queue<Audio> = LinkedList()
)