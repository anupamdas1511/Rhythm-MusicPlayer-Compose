package com.anupam.musicplayer.presentations.screens

import android.graphics.drawable.Icon
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
//import coil.compose.rememberAsyncImagePainter
//import coil.request.ImageRequest
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaItem
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.presentations.components.DashboardPlayer
import com.anupam.musicplayer.presentations.components.FloatingBottomPlayer
import com.anupam.musicplayer.presentations.components.rememberSearchState
import com.anupam.musicplayer.viewmodels.MediaEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicDashboard(
    mediaState: Flow<MediaState>,
    onEvent: (MediaEvent) -> Unit,
    navController: NavController
) {
    val state = mediaState.collectAsState(initial = MediaState())
    val context = LocalContext.current
//    val searchState = rememberSearchState()
//    var isSearching = true
    Scaffold(
        topBar = {
//            var sizeTopBar by remember { mutableStateOf(IntSize.Zero) }
//            var positionInRootTopBar by remember { mutableStateOf(Offset.Zero) }
            Column {
                androidx.compose.material3.TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,

                        ),
                    title = { Text(text = "MusicPlayer") },
                    actions = {
                        IconButton(onClick = {
                            onEvent(MediaEvent.ScanMedia(context = context))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.scanner),
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
//                            if (searchState.isSearchModeEnabled) searchState.enableSearchMode() else searchState.disableSearchMode()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        }
                    },
//                    modifier = Modifier
//                        .onGloballyPositioned { layoutCoordinates ->
//                            sizeTopBar = layoutCoordinates.size
//                            positionInRootTopBar = layoutCoordinates.positionInRoot()
//                        }
                )
//                if (searchState.isSearchModeEnabled) {
//                    SearchBar(
//                        query = "",
//                        onQueryChange = {},
//                        onSearch = {},
//                        active = isSearching,
//                        onActiveChange = {}
//                    ) {
//
//                    }
//                }
            }
        },
        bottomBar = {
//            FloatingBottomPlayer(
//                state = state.value,
//                onEvent = onEvent,
//                navController = navController
//            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
        ) {
            DashboardPlayer(
                state = state.value,
                onEvent = onEvent,
                navController = navController
            )

            Text(
                text = "All Music",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
            )
//            var currentPlayingIndex = remember { mutableIntStateOf(-1) }
            LazyColumn(
                modifier = Modifier
//                    .padding(vertical = it.calculateTopPadding())
            ) {
                itemsIndexed(
                    items = state.value.mediaFiles,
                    key = { _, mediaItem ->
                        mediaItem.id
                    }
                ) { index, mediaItem ->
                    MusicItem(
                        audio = mediaItem,
                        index = index,
                        isPlaying = index == (state.value.currentMedia ?: -1),
                        onClick = {
                            onEvent(MediaEvent.SelectMedia(index, context))
//                            currentPlayingIndex.intValue = index
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MusicItem(
    modifier: Modifier = Modifier,
    audio: MediaItem,
    index: Int = 0,
    isPlaying: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            }
            .padding(10.dp)
    ) {
        Column(
            modifier.weight(10f)
        ) {
            Text(
                text = audio.name,
                color = if (isPlaying) Color.Cyan else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            Text(
                text = audio.artist ?: "Unknown Artist",
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
//        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {
            // todo: unimplemented
        }) {
            if (isPlaying) {
                Icon(
                    imageVector = Icons.Sharp.MusicNote,
                    contentDescription = null,
                    tint = Color.Cyan
                )
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = ImageRequest.Builder(context = LocalContext.current)
//                            .data(R.drawable.music_wave)
//                            .crossfade(true)
//                            .build()
//                    ),
//                    contentDescription = null
//                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.add_to_queue),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}


//@Preview
//@Composable
//private fun MusicItemPreview() {
//    MusicItem(
//        audio = MediaItem(
//            0,
//            "This is a very long text that will be clipped if exceeds the fixed boundary",
//            null,
//            Uri.EMPTY,
//            "Anupam Das",
//            12345,
//            0L
//        ),
//        onClick = {}
//    )
//}

@Preview
@Composable
private fun MediaDashboardPreview() {
    MusicDashboard(
        mediaState = flowOf(MediaState()),
        onEvent = {},
        navController = rememberNavController()
    )
}