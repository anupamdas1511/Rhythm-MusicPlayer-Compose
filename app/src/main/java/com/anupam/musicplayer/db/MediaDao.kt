package com.anupam.musicplayer.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.anupam.musicplayer.data.MediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT EXISTS(SELECT 1 FROM media_items WHERE id = :id)")
    suspend fun exists(id: Long): Boolean
    @Upsert
    suspend fun saveMedia(media: MediaItem)
    @Delete
    suspend fun deleteMedia(media: MediaItem)
    @Query("SELECT * FROM media_items ORDER By name")
    fun getAllMediaByTitle(): Flow<List<MediaItem>>
}