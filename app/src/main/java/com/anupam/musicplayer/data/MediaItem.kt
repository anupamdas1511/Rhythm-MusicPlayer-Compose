package com.anupam.musicplayer.data

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "media_items")
@TypeConverters(value = [UriConverters::class])
data class MediaItem(
    @PrimaryKey val id: Long,
    val name: String,
//    val cover: Bitmap?,
    val contentUri: Uri,
    val artist: String?,
    val dateAdded: Long,
    val duration: Long,
    val favorite: Boolean = false
)

class UriConverters {
    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }
}
