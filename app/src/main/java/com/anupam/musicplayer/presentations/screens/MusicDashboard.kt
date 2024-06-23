package com.anupam.musicplayer.presentations.screens

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaItem
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.data.TabItem
import com.anupam.musicplayer.modes.MediaListMode
import com.anupam.musicplayer.presentations.components.DashboardPlayer
import com.anupam.musicplayer.presentations.components.MusicItem
import com.anupam.musicplayer.viewmodels.MediaEvent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MusicDashboard(
    mediaState: Flow<MediaState>,
    onEvent: (MediaEvent) -> Unit,
    navController: NavController,
    isTabletMode: Boolean = false
) {
    val state = mediaState.collectAsState(initial = MediaState())
    val context = LocalContext.current

    val tabItems = listOf(
        TabItem(title = "All Songs"),
        TabItem(title = "Favorite Songs")
    )
    Scaffold(
        topBar = {
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
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
        ) {
            DashboardPlayer(
                state = state.value,
                onEvent = onEvent,
                navController = navController,
                isTabletMode = isTabletMode
            )

            var selectedTabRow by remember { mutableIntStateOf(0) }
            val pagerState = rememberPagerState { tabItems.size }
            LaunchedEffect(selectedTabRow) {
                pagerState.animateScrollToPage(selectedTabRow)
            }
            LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                if (!pagerState.isScrollInProgress)
                    selectedTabRow = pagerState.currentPage
            }

            TabRow(selectedTabIndex = selectedTabRow) {
                tabItems.forEachIndexed { index, tabItem ->
                    Tab(
                        selected = index == selectedTabRow,
                        onClick = { selectedTabRow = index },
                        text = {
                            Text(
                                text = tabItem.title,
                                style = if (index == selectedTabRow) {
                                    MaterialTheme.typography.titleLarge
                                } else {
                                    MaterialTheme.typography.titleSmall
                                }
                            )
                        }
                    )
                }
            }
            MediaListPager(pagerState = pagerState, state = state.value, onEvent = onEvent)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.MediaListPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    state: MediaState,
    onEvent: (MediaEvent) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .weight(1f)
    ) { index ->
        when (index) {
            MediaListMode.ALL_SONGS.ordinal -> {
                MediaList(
                    state = state,
                    list = state.mediaFiles,
                    onEvent = onEvent,
                    mode = MediaListMode.ALL_SONGS
                )
            }

            MediaListMode.FAVORITE_SONGS.ordinal -> {
                MediaList(
                    state = state,
                    list = state.favoriteMediaFiles,
                    onEvent = onEvent,
                    mode = MediaListMode.FAVORITE_SONGS
                )
            }
        }
    }
}

@Composable
fun MediaList(
    modifier: Modifier = Modifier,
    state: MediaState,
    list: List<MediaItem>,
    onEvent: (MediaEvent) -> Unit,
    mode: MediaListMode
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(
            items = list,
            key = { _, mediaItem ->
                mediaItem.id
            }
        ) { index, mediaItem ->
            MusicItem(
                audio = mediaItem,
                index = index,
                isPlaying = index == (state.currentMedia ?: -1),
                onClick = {
                    onEvent(MediaEvent.SelectMedia(index, context, mode))
                }
            )
        }
    }
}


@Preview
@Composable
private fun MusicItemPreview() {
    MusicItem(
        audio = MediaItem(
            0,
            "This is a very long text that will be clipped if exceeds the fixed boundary",
            Uri.EMPTY,
            "Anupam Das",
            12345,
            0L
        ),
        onClick = {}
    )
}

//@Preview
//@Composable
//private fun MediaDashboardPreview() {
//    MusicDashboard(
//        mediaState = flowOf(MediaState()),
//        onEvent = {},
//        navController = rememberNavController()
//    )
//}