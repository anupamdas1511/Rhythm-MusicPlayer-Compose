package com.anupam.musicplayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.presentations.screens.MusicDashboard
import com.anupam.musicplayer.presentations.screens.MusicPlayer
import com.anupam.musicplayer.viewmodels.MediaViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val mediaViewModel: MediaViewModel = viewModel()

    mediaViewModel.initializeListIfNeeded(context)

    //  !! It lags too much like crazy !!
//    LaunchedEffect(key1 = null) {
//        mediaViewModel.loadBitmapIfNeeded(context)
//    }
//    ? Log.d("Kuch toh debug", "Hello " + mediaViewModel.state.collectAsState(MediaState()).value.mediaFiles)
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            MusicDashboard(
                mediaState = mediaViewModel.state,
                onEvent = mediaViewModel::onEvent
            )
        }
        composable("player") {
            MusicPlayer()
        }
    }
}