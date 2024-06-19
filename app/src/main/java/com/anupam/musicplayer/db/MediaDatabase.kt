package com.anupam.musicplayer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anupam.musicplayer.data.MediaItem

@Database(entities = [MediaItem::class], version = 1)
abstract class MediaDatabase: RoomDatabase() {
    abstract fun mediaDao(): MediaDao
}