package com.anupam.musicplayer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anupam.musicplayer.presentations.screens.MusicPlayer
import com.anupam.musicplayer.viewmodels.MediaViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val mediaViewModel: MediaViewModel = viewModel()

    NavHost(navController = navController, startDestination = "player") {
        composable("player") {
            MusicPlayer()
        }
    }
}