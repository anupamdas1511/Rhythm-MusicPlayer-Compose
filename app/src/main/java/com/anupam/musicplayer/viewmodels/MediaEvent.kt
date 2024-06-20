package com.anupam.musicplayer.viewmodels

import android.content.Context
import com.anupam.musicplayer.data.MediaItem

sealed class MediaEvent {
    data class SelectMedia(val mediaIndex: Int, val context: Context): MediaEvent()
    data object PlayAudio: MediaEvent()
    data object PauseAudio: MediaEvent()
    data class NextAudio(val context: Context): MediaEvent()
    data class PreviousAudio(val context: Context): MediaEvent()
    data class SearchMedia(val query: String): MediaEvent()
    data class SeekMedia(val position: Int): MediaEvent()
    data class ScanMedia(val context: Context): MediaEvent()
    data object AddToFavorite: MediaEvent()
}