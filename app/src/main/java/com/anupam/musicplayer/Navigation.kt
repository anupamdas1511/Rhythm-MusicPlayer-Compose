package com.anupam.musicplayer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anupam.musicplayer.presentations.screens.MusicDashboard
import com.anupam.musicplayer.presentations.screens.MusicPlayer
import com.anupam.musicplayer.viewmodels.MediaViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val mediaViewModel: MediaViewModel = viewModel()

//    LaunchedEffect(Unit) {
//        mediaViewModel.initializeListIfNeeded(context)
//    }

    //  !! It lags too much like crazy !!
//    LaunchedEffect(key1 = null) {
//        mediaViewModel.loadBitmapIfNeeded(context)
//    }
//    ? Log.d("Kuch toh debug", "Hello " + mediaViewModel.state.collectAsState(MediaState()).value.mediaFiles)
    NavHost(navController = navController, startDestination = NavigationItem.Dashboard.route) {
        composable(NavigationItem.Dashboard.route) {
            MusicDashboard(
                mediaState = mediaViewModel.state,
                onEvent = mediaViewModel::onEvent,
                navController = navController
            )
        }
        composable(NavigationItem.Player.route) {
            MusicPlayer(
                onEvent = mediaViewModel::onEvent,
                mediaState = mediaViewModel.state,
                navController = navController
            )
        }
    }
}

enum class Screen {
    DASHBOARD,
    PLAYER
}

sealed class NavigationItem(val route: String) {
    data object Dashboard: NavigationItem(Screen.DASHBOARD.name)
    data object Player: NavigationItem(Screen.PLAYER.name)
}