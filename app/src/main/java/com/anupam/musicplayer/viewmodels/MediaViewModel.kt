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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.data.MediaItem
import com.anupam.musicplayer.db.MediaDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
//import linc.com.amplituda.Amplituda
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val dao: MediaDao,
    private val contentResolver: ContentResolver
) : ViewModel() {
    private val _audioList: StateFlow<List<MediaItem>> = dao.getAllMediaByTitle()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//    val audioList: StateFlow<List<MediaItem>> = _audioList
    private var _initialized = false
    private val _backgroundScope = viewModelScope.plus(Dispatchers.Default)
    private var _isSDCardAvailable = false
    private var timerJob: Job? = null

    private var mediaPlayer: MediaPlayer? = null
    private var isMediaPlayerPrepared = false
    private var _currentPosition = MutableStateFlow(0L)
    private var _currentMediaIndex = MutableStateFlow(0)
    private var _backgroundColor = MutableStateFlow(Color.Black)
    private var _cover = MutableStateFlow<Bitmap?>(null)

    private var _amplitudes = MutableStateFlow<List<Int>>(emptyList())

//    private val _videoList = MutableStateFlow<List<MediaItem>>(emptyList())
//    val videoList: StateFlow<List<MediaItem>> = _videoList

    private val _state = MutableStateFlow(MediaState())
    val state = combine(
        _state, _audioList, _currentPosition, _backgroundColor, _cover
    ) { state, audioList, currentPosition, backgroundColor, cover ->
        state.copy(
            mediaFiles = audioList,
            currentPosition = currentPosition,
            backgroundColor = backgroundColor,
            cover = cover
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MediaState())

    init {
//        Log.d("Kuch toh debug", "Media started")
        initMediaPlayer()
        startPositionUpdateCoroutine()
    }

    private fun initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
//                setOnPreparedListener {
//                    seekTo(_currentPosition.value.toInt())
//                    start()
//                    isMediaPlayerPrepared = true
//                    _state.update { it.copy(isPlaying = true) }
//                }
            }
        }
    }

    private fun startPositionUpdateCoroutine() {
//        timerJob?.cancel()
        if (timerJob == null) {
            timerJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    updateStateWithCurrentPosition()
                }
            }
            timerJob?.start()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        mediaPlayer?.release()
    }

    private fun updateStateWithCurrentPosition() {
//        Log.d("Kuch toh debug", "Time: ${_currentPosition.value}")
        val currentPosition: Long = mediaPlayer?.currentPosition?.toLong() ?: 0L
        _currentPosition.value = currentPosition
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onEvent(event: MediaEvent) {
//        Log.d("Kuch toh debug", "Time: $_currentPosition")
        when (event) {
            // Play Next Audio
            is MediaEvent.NextAudio -> {
                val newPosition = (_currentMediaIndex.value + 1) % _audioList.value.size
                onEvent(
                    MediaEvent.SelectMedia(
                        context = event.context,
                        mediaIndex = newPosition
                    )
                )
            }

            // Play Previous Audio
            is MediaEvent.PreviousAudio -> {
                val newPosition = (_currentMediaIndex.value - 1) % _audioList.value.size
                onEvent(
                    MediaEvent.SelectMedia(
                        context = event.context,
                        mediaIndex = newPosition
                    )
                )
            }

            // Pause Audio
            MediaEvent.PauseAudio -> {
                _state.update {
                    it.copy(
                        isPlaying = false
                    )
                }
                mediaPlayer?.pause()
            }

            // Play Audio
            MediaEvent.PlayAudio -> {
//                Log.d("Kuch toh debug", "Current Position: ${_currentPosition.value}")
                _state.update {
                    it.copy(
                        isPlaying = true
                    )
                }
                mediaPlayer?.start()
            }

            // Select Media
            is MediaEvent.SelectMedia -> {
//                Log.d("Kuch toh debug", "Media selected")
                _currentMediaIndex.value = event.mediaIndex
                _state.update {
                    it.copy(
                        currentMedia = event.mediaIndex,
                        title = _audioList.value[event.mediaIndex].name,
                        artist = _audioList.value[event.mediaIndex].artist ?: "Unknown Artist"
                    )
                }
                loadBitmapIfNeeded(context = event.context, index = event.mediaIndex)

                val audioPath = getMediaFile(_audioList.value[event.mediaIndex].id)

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
//                        release()
//                        isMediaPlayerPrepared = false
                        onEvent(MediaEvent.NextAudio(context = event.context))
                    }
                }

//                getMediaAmplitudes(event.context, audioPath)
                _state.update { it.copy(isPlaying = true) }
                Log.d("Kuch toh debug", "MediaPlayer working just fine")
            }
            // Search Media
            is MediaEvent.SearchMedia -> {

            }
            // Seek Media
            is MediaEvent.SeekMedia -> {
                _currentPosition.value = event.position.toLong()
                mediaPlayer?.seekTo(_currentPosition.value.toInt())
            }
        }
    }

    private fun getMediaAmplitudes(context: Context, audioPath: String?) {
//        val amplituda = Amplituda(context)
//        val processingOutput = amplituda.processAudio(audioPath)
//        val result = processingOutput.get()
//        val list = result.amplitudesAsList()
//        _amplitudes.value = list
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun initializeListIfNeeded(context: Context) {
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
//        _audioList.value = mediaFiles
//        _audioList.value = dao.getAllMediaByTitle().first()
        _initialized = true
    }

    private suspend fun queryMediaStore(
        context: Context,
        volumeName: String,
        mediaFiles: MutableList<MediaItem>
    ) {
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

                if (mimeType.equals("audio/mpeg")) {
//                    mediaFiles.add(MediaItem(id, title, uri, artist, dateAddedId, duration))
                    if (!dao.exists(id))
                        dao.saveMedia(MediaItem(id, title, uri, artist, dateAddedId, duration))
                }
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

    private fun loadBitmapIfNeeded(context: Context, index: Int) {
//        if (_audioList.value[index].cover != null) return
        // if this is gonna lag during scrolling, you can move it on a background thread
        _backgroundScope.launch {
            val bitmap = getAlbumArt(context, _audioList.value[index].contentUri)
//            Log.d("Kuch toh debug", "Bitmap loaded: $bitmap")
            updateBackgroundColor(bitmap)
            val mediaFiles = _audioList.value.toMutableList()
//            mediaFiles[index] = mediaFiles[index].copy(cover = bitmap)
            _cover.value = bitmap
//            _audioList.value = mediaFiles
        }

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
//        Log.d("Kuch toh debug", "Is $uri valid!!")
        val mmr = MediaMetadataRetriever()
        try {
            mmr.setDataSource(context, uri)
            val data = mmr.embeddedPicture
//            Log.d("Kuch toh debug", "Is Data null?? ${data == null}")
            return if (data != null) {
                BitmapFactory.decodeByteArray(data, 0, data.size)
            } else {
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

    private fun updateBackgroundColor(bitmap: Bitmap) {
        val colorInt = Palette.from(bitmap).generate().getDarkVibrantColor(Color.DarkGray.toArgb())
        _backgroundColor.value = Color(colorInt)
    }

    private fun checkPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
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