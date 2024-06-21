package com.anupam.musicplayer.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.anupam.musicplayer.MainActivity
import com.anupam.musicplayer.PlaybackActionReceiver
import com.anupam.musicplayer.R

class PlayerNotificationService(
//    private val context: Context
): Service() {
//    private val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    private lateinit var mediaSession: MediaSessionCompat

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mediaTitle = intent?.getStringExtra("mediaTitle") ?: "Unknown Title"
        val mediaArtist = intent?.getStringExtra("mediaArtist") ?: "Unknown Artist"
        // ? Start Foreground Service
        startForeground(1, createNotification(mediaTitle, mediaArtist))
        Log.d("kuch toh debug", "Service Started")
        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
//        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification(mediaTitle: String, mediaArtist: String): Notification {
        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val activityPendingIntent = PendingIntent.getActivity(
            this,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, PlaybackActionReceiver::class.java).apply {
            action = PlaybackActionReceiver.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            2,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopAction = Notification.Action.Builder(
            Icon.createWithResource(this, R.drawable.ic_close), "Stop", stopPendingIntent
        ).build()

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(mediaTitle)
            .setContentText(mediaArtist)
            .setSmallIcon(R.drawable.music)
//            .setPriority(Notification.PRIORITY_LOW)
//            .setStyle(NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setContentIntent(activityPendingIntent)
            .setOngoing(true) // * Notification cannot be dismissed by the user
            .addAction(stopAction)
            .build()

//        notificationManager.notify(1, notification)
        return notification
    }

    companion object {
        const val CHANNEL_ID = "media_playback_channel"
    }
}