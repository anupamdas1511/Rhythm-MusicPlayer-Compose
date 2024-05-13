package com.anupam.musicplayer.presentations.screens

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.anupam.musicplayer.data.MediaItem
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.viewmodels.MediaEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun MusicDashboard(
    mediaState: Flow<MediaState>,
    onEvent: (MediaEvent) -> Unit
) {
    val state = mediaState.collectAsState(initial = MediaState()).value
//    Log.d("Kuch toh debug", "Hello " + state.mediaFiles.toString())
    LazyColumn {
        items(state.mediaFiles) {mediaItem ->
            Text(text = mediaItem.toString())
        }
    }
}