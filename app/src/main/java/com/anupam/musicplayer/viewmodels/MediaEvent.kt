package com.anupam.musicplayer.viewmodels

import com.anupam.musicplayer.data.MediaItem

sealed class MediaEvent {
    data class SelectMedia(val media: MediaItem): MediaEvent()
    data object PlayAudio: MediaEvent()
    data object PauseAudio: MediaEvent()
    data object NextAudio: MediaEvent()
}