package com.anupam.musicplayer.presentations.screens

import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.sharp.Pause
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.anupam.musicplayer.NavigationItem
import com.anupam.musicplayer.R
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.presentations.components.CustomSlider
import com.anupam.musicplayer.presentations.components.GlowingCard
import com.anupam.musicplayer.presentations.components.drawNeonStroke
import com.anupam.musicplayer.utils.formatTime
import com.anupam.musicplayer.viewmodels.MediaEvent
//import com.smarttoolfactory.slider.ColorfulSlider
//import com.linc.audiowaveform.AudioWaveform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Arrow",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = "Add List",
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongDescription(
    title: String,
    artist: String,
    state: MediaState,
    onEvent: (MediaEvent) -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(.2f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .basicMarquee()
            )

            CompositionLocalProvider {
                Text(
                    text = artist,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .basicMarquee()
                )
            }
        }

        IconButton(onClick = {
            onEvent(MediaEvent.AddToFavorite)
        }) {
            Icon(
                imageVector = if (state.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerSlider(
    onEvent: (MediaEvent) -> Unit,
    duration: Long = 100,
    currentPosition: Long = 30
) {
    var sliderPosition by remember {
        mutableFloatStateOf(currentPosition.toFloat() / duration)
    }

    LaunchedEffect(key1 = currentPosition) {
        sliderPosition = currentPosition.toFloat() / duration
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
//        Slider(
//            value = sliderPosition,
//            onValueChange = {
//                sliderPosition = it
//                onEvent(MediaEvent.SeekMedia((it * duration).toInt()))
//            },
//            colors = SliderDefaults.colors(
//                thumbColor = Color.White,
//                activeTrackColor = Color.Cyan,
//            ),
//            thumb = {
//                SliderDefaults.Thumb(
//                    interactionSource = interactionSource,
//                    modifier = Modifier
//                        .drawWithContent {
//                            drawNeonStroke(radius = 200.dp, color = Color.Blue)
//                            drawContent()
//                        }
//                )
//            },
//            track = {
//                SliderDefaults.Track(
//                    sliderPositions = it,
//                    modifier = Modifier
//                        .drawWithContent {
//                            drawContent()
//                        }
//                )
//            }
//        )
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onEvent(MediaEvent.SeekMedia((it * duration).toInt()))
            }
        )
//        CustomSlider(value = sliderPosition, onValueChange = {
//            sliderPosition = it
//            onEvent(MediaEvent.SeekMedia((it * duration).toInt()))
//        })
//        ColorfulSlider(
//            value = sliderPosition,
//            onValueChange = { it ->
//                sliderPosition = it
//                onEvent(MediaEvent.SeekMedia((it * duration).toInt()))
//            }
//        )
//        var waveformProgress by remember { mutableStateOf(0F) }
//        AudioWaveform(
//            amplitudes = amplitudes,
//            progress = sliderPosition,
//            onProgressChange = {
//                sliderPosition = it
//                onEvent(MediaEvent.SeekMedia((it * duration).toInt()))
//            }
//        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = formatTime(currentPosition), color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = formatTime(duration), color = Color.White)
        }
    }
}

@Composable
fun PlayerButtons(
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 60.dp,
    sideButtonSize: Dp = 42.dp,
    onEvent: (MediaEvent) -> Unit,
    state: MediaState,
    onPlayPauseClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
    isPlaying: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val buttonModifier = Modifier
            .size(sideButtonSize)
            .semantics { role = Role.Button }

        IconButton(onClick = onPreviousClicked) {
            Image(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Skip Previous",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = buttonModifier
            )
        }

        Image(
            imageVector = Icons.Filled.Replay10,
            contentDescription = "Reply 10 Second",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = buttonModifier
        )

//        IconButton(
//            onClick = onPlayPauseClicked,
//            modifier = Modifier
//                .size(playerButtonSize)
//                .drawWithContent {
//                    drawNeonStroke(radius = 80.dp, color = Color.Blue)
//                    drawContent()
//                }
//        ) {
//            Image(
//                imageVector = if (isPlaying) Icons.Filled.PauseCircleFilled else Icons.Sharp.PlayArrow,
//                contentDescription = "Play",
//                contentScale = ContentScale.Fit,
//                colorFilter = ColorFilter.tint(Color.Blue),
//                modifier = Modifier
//                    .size(playerButtonSize)
//                    .semantics { role = Role.Button }
//            )
//        }

        Button(
            onClick = onPlayPauseClicked,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue
            ),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .size(playerButtonSize * 1.2f)
                .drawWithContent {
                    drawNeonStroke(radius = 100.dp, color = Color.Blue)
                    drawContent()
                }
        ) {
            Image(
                imageVector = if (isPlaying) Icons.Sharp.Pause else Icons.Sharp.PlayArrow,
                contentDescription = "Play",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(playerButtonSize)
                    .semantics { role = Role.Button }
            )
        }


        Image(
            imageVector = Icons.Filled.Forward10,
            contentDescription = "Forward 10 Seconds",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = buttonModifier
        )

        IconButton(onClick = onNextClicked) {
            Image(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Skip Next",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = buttonModifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MusicPlayer(
    mediaState: Flow<MediaState>,
    onEvent: (MediaEvent) -> Unit,
//    title: String,
    navController: NavController
) {
    val context = LocalContext.current
    val state = mediaState.collectAsState(initial = MediaState())
    var duration by remember { mutableLongStateOf(100L) }
    var currentPosition by remember { mutableLongStateOf(state.value.currentPosition) }

    if (state.value.currentMedia != null) {
        LaunchedEffect(key1 = state.value.mediaFiles[state.value.currentMedia!!].duration) {
            duration = state.value.mediaFiles[state.value.currentMedia!!].duration
        }
    }
    LaunchedEffect(key1 = state.value.currentPosition) {
        currentPosition = state.value.currentPosition
    }

    val defaultBackground = BitmapFactory.decodeResource(context.resources, R.drawable.musics)
    val image = if (state.value.currentMedia != null) {
        state.value.cover ?: defaultBackground
    } else defaultBackground

    Scaffold (
        topBar = {
            androidx.compose.material3.TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(NavigationItem.Dashboard.route) }) {
                        Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = null)
                    }
                },
                title = { /*TODO*/ },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(0xFF0B081D))
                .padding(vertical = it.calculateTopPadding(), horizontal = 10.dp)
        ) {
//            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(30.dp))
            GlowingCard(
                modifier = Modifier
                    .weight(15f),
                glowingColor = state.value.backgroundColor,
                containerColor = Color.Transparent
            ) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "Music Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .sizeIn(maxWidth = 700.dp, maxHeight = 700.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
//                        .weight(10f)
                )
            }
//            Image(
//                bitmap = image.asImageBitmap(),
//                contentDescription = "Music Banner",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
//                    .aspectRatio(1f)
//                    .clip(CircleShape)
//                        .weight(10f)
//            )
            Spacer(modifier = Modifier.height(30.dp))
            SongDescription(title = state.value.title, artist = state.value.artist, state = state.value, onEvent = onEvent)
            Spacer(modifier = Modifier.height(35.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(10f)
            ) {
                PlayerSlider(
                    onEvent = onEvent,
                    duration = duration,
                    currentPosition = currentPosition
                )
                Spacer(modifier = Modifier.height(20.dp))
                PlayerButtons(
                    modifier = Modifier.padding(vertical = 8.dp),
                    onEvent = onEvent,
                    state = state.value,
                    onPlayPauseClicked = { onEvent(if (state.value.isPlaying) MediaEvent.PauseAudio else MediaEvent.PlayAudio) },
                    onPreviousClicked = { onEvent(MediaEvent.PreviousAudio(context)) },
                    onNextClicked = { onEvent(MediaEvent.NextAudio(context)) },
                    isPlaying = state.value.isPlaying
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun MusicPlayerPreview() {
    MusicPlayer(
        mediaState = flowOf(MediaState(
            title = "This is a Song title that is very long",
            artist = "This is a artist"
        )),
        navController = rememberNavController(),
        onEvent = {}
    )
}