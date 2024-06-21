package com.anupam.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anupam.musicplayer.services.PlayerNotificationService

class PlaybackActionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_STOP) {
            context?.stopService(Intent(context, PlayerNotificationService::class.java))
        }
    }

    companion object {
        const val ACTION_STOP = "com.anupam.musicplayer.ACTION_STOP"
    }
}