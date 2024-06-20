package com.anupam.musicplayer

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.anupam.musicplayer.db.MIGRATION_1_2
import com.anupam.musicplayer.db.MediaDao
import com.anupam.musicplayer.db.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver = context.contentResolver

    @Provides
    fun provideMediaDao(mediaDatabase: MediaDatabase): MediaDao = mediaDatabase.mediaDao()

    @Provides
    @Singleton
    fun provideMediaDatabase(@ApplicationContext appContext: Context): MediaDatabase {
        return Room.databaseBuilder(
            appContext,
            MediaDatabase::class.java,
            "media"
        )
            .addMigrations(MIGRATION_1_2).build()
    }
}