package com.anupam.musicplayer

import android.media.AudioAttributes
import android.media.MediaPlayer

class Player {
    private var mediaPlayer: MediaPlayer? = null

    init {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
    }
}