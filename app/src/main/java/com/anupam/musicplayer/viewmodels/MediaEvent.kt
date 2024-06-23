package com.anupam.musicplayer.viewmodels

import android.content.Context
import com.anupam.musicplayer.data.MediaItem
import com.anupam.musicplayer.modes.MediaListMode

sealed class MediaEvent {
    data class SelectMedia(val mediaIndex: Int, val context: Context, val mode: MediaListMode): MediaEvent()
    data class SearchSelectMedia(val media: MediaItem, val context: Context): MediaEvent()
    data object PlayAudio: MediaEvent()
    data object PauseAudio: MediaEvent()
    data class NextAudio(val context: Context): MediaEvent()
    data class PreviousAudio(val context: Context): MediaEvent()
    data class QueryChange(val query: String): MediaEvent()
    data object SearchMedia: MediaEvent()
    data class SeekMedia(val position: Int): MediaEvent()
    data class ScanMedia(val context: Context): MediaEvent()
    data object AddToFavorite: MediaEvent()
}