package com.anupam.musicplayer

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anupam.musicplayer.presentations.screens.MusicDashboard
import com.anupam.musicplayer.presentations.screens.MusicPlayer
import com.anupam.musicplayer.presentations.screens.PermissionScreen
import com.anupam.musicplayer.viewmodels.MediaViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val mediaViewModel: MediaViewModel = viewModel()
    val mediaState = mediaViewModel.state.collectAsState()
    val dialogQueue = mediaViewModel.visiblePermissionDialogQueue
    val permissionsToRequest = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.FOREGROUND_SERVICE,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Manifest.permission.MANAGE_EXTERNAL_STORAGE else Manifest.permission.READ_EXTERNAL_STORAGE
    )

    LaunchedEffect(key1 = mediaState.value.currentMedia) {
        mediaViewModel.startMediaPlayback()
    }

//    LaunchedEffect(Unit) {
//        mediaViewModel.initializeListIfNeeded(context)
//    }

    //  !! It lags too much like crazy !!
//    LaunchedEffect(key1 = null) {
//        mediaViewModel.loadBitmapIfNeeded(context)
//    }
//    ? Log.d("Kuch toh debug", "Hello " + mediaViewModel.state.collectAsState(MediaState()).value.mediaFiles)
    NavHost(navController = navController, startDestination = NavigationItem.PermissionScreen.route) {
        composable(NavigationItem.PermissionScreen.route) {
            PermissionScreen(
                onPermissionEvent = mediaViewModel::onPermissionEvent,
                navController = navController,
                dialogQueue = dialogQueue,
                permissionsToRequest = permissionsToRequest
            )
        }
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
    PLAYER,
    PERMISSION_SCREEN
}

sealed class NavigationItem(val route: String) {
    data object Dashboard: NavigationItem(Screen.DASHBOARD.name)
    data object Player: NavigationItem(Screen.PLAYER.name)
    data object PermissionScreen: NavigationItem(Screen.PERMISSION_SCREEN.name)
}