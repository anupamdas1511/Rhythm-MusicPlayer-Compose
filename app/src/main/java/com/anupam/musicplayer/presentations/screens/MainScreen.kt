package com.anupam.musicplayer.presentations.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.data.TabItem
import com.anupam.musicplayer.modes.TabMode
import com.anupam.musicplayer.utils.isTablet
import com.anupam.musicplayer.viewmodels.MediaEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mediaState: Flow<MediaState>,
    onEvent: (MediaEvent) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val isTablet = context.isTablet()
    val state = mediaState.collectAsState(initial = MediaState())
    val tabItems = listOf(
        TabItem(
            title = "Dashboard",
            unselectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home
        ),
        TabItem(
            title = "Search",
            unselectedIcon = Icons.Outlined.Search,
            selectedIcon = Icons.Filled.Search
        ),
        TabItem(
            title = "Settings",
            unselectedIcon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings
        )
    )
    var selectedTabRow by remember {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        tabItems.size
    }
    LaunchedEffect(selectedTabRow) {
        pagerState.animateScrollToPage(selectedTabRow)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            selectedTabRow = pagerState.currentPage
    }
    Column (
        modifier = modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {index ->
            when (index) {
                TabMode.DASHBOARD_MODE.ordinal -> {
                    MusicDashboard(mediaState = mediaState, onEvent = onEvent, navController = navController, isTabletMode = isTablet)
                }
                TabMode.SEARCH_MODE.ordinal -> {
                    SearchScreen(
                        onEvent = onEvent,
                        state = state.value
                    )
                }
                TabMode.SETTINGS_MODE.ordinal -> {

                }
            }
        }
        TabRow(selectedTabIndex = selectedTabRow) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabRow,
                    onClick = {
                        selectedTabRow = index
                    },
                    text = {
                        Text(text = item.title)
                    },
                    icon = {
                        (if (index == selectedTabRow) item.selectedIcon else item.unselectedIcon)?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = item.title
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview(device = Devices.DEFAULT)
@Composable
private fun MainScreenPreview() {
    MainScreen(
        mediaState = flowOf(MediaState()),
        onEvent = {},
        navController = rememberNavController()
    )
}