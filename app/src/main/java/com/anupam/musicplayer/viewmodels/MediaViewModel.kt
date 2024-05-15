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
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.data.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    private val _backgroundScope = viewModelScope.plus(Dispatchers.Default)
    private var _isSDCardAvailable = false
    private var timerJob: Job? = null

    private var mediaPlayer: MediaPlayer? = null
    private var isMediaPlayerPrepared = false
    private var _currentPosition = MutableStateFlow<Long>(0L)

//    private val _videoList = MutableStateFlow<List<MediaItem>>(emptyList())
//    val videoList: StateFlow<List<MediaItem>> = _videoList

    private val _state = MutableStateFlow(MediaState())
    val state = combine(_state, _audioList, _currentPosition) { state, audioList, currentPosition ->
        state.copy(
            mediaFiles = audioList,
            currentPosition = currentPosition
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MediaState())

    init {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        startPositionUpdateCoroutine()
    }

    private fun startPositionUpdateCoroutine() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                updateStateWithCurrentPosition()
            }
        }
        timerJob?.start()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    private fun updateStateWithCurrentPosition() {
        val currentPosition: Long = mediaPlayer?.currentPosition?.toLong() ?: 0L
        _currentPosition.value = currentPosition
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onEvent(event: MediaEvent) {
        Log.d("Kuch toh debug", "Time: $_currentPosition")
        when(event) {
            MediaEvent.NextAudio -> TODO()
            MediaEvent.PauseAudio -> {
                _state.update { it.copy(
                    isPlaying = false
                ) }
                mediaPlayer?.pause()
            }
            MediaEvent.PlayAudio -> {
                _state.update { it.copy(
                    isPlaying = true
                ) }
                mediaPlayer?.start()
            }
            is MediaEvent.SelectMedia -> {
                _state.update { it.copy(
                    currentMedia = event.media
                ) }

                val audioPath = getMediaFile(event.media.id)
                Log.d("Kuch toh debug", "MediaPlayer Testing ${event.media.contentUri}")
                mediaPlayer?.apply {
                    if (isMediaPlayerPrepared || isPlaying) {
                        stop()
                        reset()
                    }
                    setDataSource(audioPath)
                    prepareAsync()
                    setOnPreparedListener {
                        start()
                        isMediaPlayerPrepared = true
                    }
                    setOnCompletionListener {
                        release()
                        isMediaPlayerPrepared = false
                    }
                }
                _state.update { it.copy(
                    isPlaying = true
                ) }
                Log.d("Kuch toh debug", "MediaPlayer working just fine")
//                mediaPlayer?.stop()
//                mediaPlayer?.reset()
            }
        }
    }

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

//        Log.d("Kuch toh debug", "Hello $mediaFiles")
        _audioList.value = mediaFiles
        _initialized = true
    }

    private fun queryMediaStore(context: Context, volumeName: String, mediaFiles: MutableList<MediaItem>) {
        val collection = MediaStore.Audio.Media.getContentUri(volumeName)
//        Log.d("Kuch toh debug", "Does $collection exists!!")
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.MIME_TYPE
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

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

            val mimetypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val duration = cursor.getLong(durationColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val dateAddedId = cursor.getLong(dateAdded)
                val mimeType = cursor.getString(mimetypeColumn)
                val uri: Uri = ContentUris.withAppendedId(collection, id)

                if (mimeType.equals("audio/mpeg"))
                    mediaFiles.add(
                        MediaItem(
                            id,
                            title,
                            null,
                            uri,
                            artist,
                            dateAddedId,
                            duration
                        ))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getMediaFile(id: Long): String? {
        val collection1 = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val collection2 = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA
        )
        var audioPath: String? = null

        contentResolver.query(
            collection1,
            projection,
            "${MediaStore.Audio.Media._ID} = ?",
            arrayOf(id.toString()),
            null
        )?.use { cursor ->
            if (cursor.moveToNext()) {
                val data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                audioPath = cursor.getString(data)
            }
        }

        if (_isSDCardAvailable) {
            contentResolver.query(
                collection2,
                projection,
                "${MediaStore.Audio.Media._ID} = ?",
                arrayOf(id.toString()),
                null
            )?.use { cursor ->
                if (cursor.moveToNext()) {
                    val data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    audioPath = cursor.getString(data)
                }
            }
        }
        return audioPath
    }

    private fun isSDCardAvailable(context: Context): Boolean {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolumes = storageManager.storageVolumes
        for (storageVolume in storageVolumes) {
            if (storageVolume.isRemovable && !storageVolume.isPrimary) {
                _isSDCardAvailable = true
                return true
            }
        }
        return false
    }

//    private fun convertMillis(duration: Int): String {
//        return duration.toString()
//    }
    fun loadBitmapIfNeeded(context: Context) {
//        if (_audioList.value[index].cover != null) return
//        // if this is gonna lag during scrolling, you can move it on a background thread
//        _backgroundScope.launch {
//            val bitmap = getAlbumArt(context, _audioList.value[index].contentUri)
////            _audioList.value[index] = _audioList.value[index].copy(cover = bitmap)
//            val mediaFiles = _audioList.value.toMutableList()
//            mediaFiles[index] = mediaFiles[index].copy(cover = bitmap)
//            _audioList.value = mediaFiles
//        }

//        _backgroundScope.launch {
//            for (index in 0..<_audioList.value.size) {
//                val bitmap = getAlbumArt(context, _audioList.value[index].contentUri)
//                val mediaFiles = _audioList.value.toMutableList()
//                mediaFiles[index] = mediaFiles[index].copy(cover = bitmap)
//                _audioList.value = mediaFiles
//            }
//        }
    }

    private fun getAlbumArt(context: Context, uri: Uri): Bitmap {
        Log.d("Kuch toh debug", "Is $uri valid!!")
        val mmr = MediaMetadataRetriever()
        try {
            mmr.setDataSource(context, uri)
            val data = mmr.embeddedPicture
            return if(data != null){
                BitmapFactory.decodeByteArray(data, 0, data.size)
            }else{
                BitmapFactory.decodeResource(context.resources, R.drawable.music_bg)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mmr.release()
        }
        return BitmapFactory.decodeResource(context.resources, R.drawable.music_bg)
    }

    private fun checkPermission(context: Context): Boolean {
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
}