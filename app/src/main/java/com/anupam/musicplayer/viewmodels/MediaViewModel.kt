package com.anupam.musicplayer.viewmodels

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.data.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val contentResolver: ContentResolver
): ViewModel() {
    private val _audioList = MutableStateFlow<List<MediaItem>>(emptyList())
    val audioList: StateFlow<List<MediaItem>> = _audioList
    private var _initialized = false
//    private var _hasPermission = false
    private val _backgroundScope = viewModelScope.plus(Dispatchers.Default)

//    private val _videoList = MutableStateFlow<List<MediaItem>>(emptyList())
//    val videoList: StateFlow<List<MediaItem>> = _videoList

    private val _state = MutableStateFlow(MediaState())
    val state = combine(_state, _audioList) { state, audioList ->
        state.copy(
            mediaFiles = audioList
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MediaState())

    fun onEvent(event: MediaEvent) {

    }

//    fun loadMediaFiles() {
//        viewModelScope.launch {
//            val mediaFiles = fetchMediaFilesFromExternalStorage()
//            val audioFiles = mediaFiles.filter { it.mimeType.startsWith("AUDIO") }
//            _audioList.value = audioFiles
////            val videoFiles = mediaFiles.filter { it.mimeType.startsWith("VIDEO") }
////            _videoList.value = videoFiles
//
//            if (audioFiles.isNotEmpty()) {
//                val firstMedia = audioFiles.first()
//                _state.value = MediaState(
//                    currentMedia = firstMedia.contentUri,
//                    title = firstMedia.name,
//                    mimeType = firstMedia.mimeType,
//                    isPlaying = false,
//                    previousAudio = null,
//                    mediaFiles = audioFiles
//                )
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun initializeListIfNeeded(context: Context) {
        if (_initialized) return
        val mediaFiles = ArrayList<MediaItem>()
        if (checkPermission(context)) {
            queryMediaStore(context, MediaStore.VOLUME_EXTERNAL, mediaFiles)

            if (isSDCardAvailable(context)) {
                queryMediaStore(context, MediaStore.VOLUME_EXTERNAL_PRIMARY, mediaFiles)
            }
        } else {
            requestPermission(context)
        }

        Log.d("Kuch toh debug", "Hello $mediaFiles")
        _audioList.value = mediaFiles
        _initialized = true
    }

    private fun queryMediaStore(context: Context, volumeName: String, mediaFiles: MutableList<MediaItem>) {

//        val f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        val collection = MediaStore.Audio.Media.getContentUri(volumeName)
        Log.d("Kuch toh debug", "Does $collection exists!!")
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATE_ADDED
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dateAdded = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val duration = cursor.getInt(durationColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val dateAddedId = cursor.getLong(dateAdded)
                val contentUri: Uri = ContentUris.withAppendedId(collection, id)

                val durationString = convertMillis(duration)
                mediaFiles.add(MediaItem(id, title, null, contentUri, artist, dateAddedId, durationString))
            }
        }
    }

    private fun isSDCardAvailable(context: Context): Boolean {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolumes = storageManager.storageVolumes
        for (storageVolume in storageVolumes) {
            if (storageVolume.isRemovable && !storageVolume.isPrimary) {
                return true
            }
        }
        return false
    }

    private fun convertMillis(duration: Int): String {
        return duration.toString()
    }
    fun loadBitmapIfNeeded(context: Context, index: Int) {
        if (_audioList.value[index].cover != null) return
        // if this is gonna lag during scrolling, you can move it on a background thread
        _backgroundScope.launch {
            val bitmap = getAlbumArt(context, _audioList.value[index].contentUri)
//            _audioList.value[index] = _audioList.value[index].copy(cover = bitmap)
            val mediaFiles = _audioList.value.toMutableList()
            mediaFiles[index] = mediaFiles[index].copy(cover = bitmap)
            _audioList.value = mediaFiles
        }
    }

    private fun getAlbumArt(context: Context, uri: Uri): Bitmap {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val data = mmr.embeddedPicture
        return if(data != null){
            BitmapFactory.decodeByteArray(data, 0, data.size)
        }else{
            BitmapFactory.decodeResource(context.resources, R.drawable.music_bg)
        }
    }

    fun checkPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", context.packageName))
            context.startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
    }


//    private fun fetchMediaFilesFromExternalStorage(): List<MediaItem> {
//        val mediaItems = mutableListOf<MediaItem>()
//
//        val projection = arrayOf(
//            MediaStore.MediaColumns._ID,
//            MediaStore.MediaColumns.DISPLAY_NAME,
//            MediaStore.MediaColumns.DATE_ADDED,
//            MediaStore.MediaColumns.MIME_TYPE
//        )
//
//        // Query for media files using ContentResolver
//        val query = contentResolver.query(
//            MediaStore.Files.getContentUri("external"),
//            projection,
//            null,
//            null,
//            "${MediaStore.MediaColumns.DATE_ADDED} DESC"
//        )
//        query?.use { cursor ->
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
//            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
//            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
//            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
//
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(idColumn)
//                val name = cursor.getString(nameColumn)
//                val dateAdded = cursor.getLong(dateAddedColumn)
//                val mimeType = cursor.getString(mimeTypeColumn)
//
//                val contentUri = MediaStore.Files.getContentUri("external").buildUpon()
//                    .appendPath(id.toString())
//                    .build()
//
////                val mediaItem = MediaItem(id, name, contentUri, dateAdded, mimeType)
////                mediaItems.add(mediaItem)
//            }
//        }
//
//        return mediaItems
//    }
}