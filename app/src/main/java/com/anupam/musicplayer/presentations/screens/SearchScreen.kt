package com.anupam.musicplayer.presentations.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.anupam.musicplayer.data.MediaItem
import com.anupam.musicplayer.data.MediaState
import com.anupam.musicplayer.presentations.components.MusicItem
import com.anupam.musicplayer.viewmodels.MediaEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onEvent: (MediaEvent) -> Unit,
    state: MediaState
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val searchItems = remember { mutableStateListOf<MediaItem>() }

    LaunchedEffect(state.searchedMediaFiles) {
        searchItems.clear()
        searchItems.addAll(state.searchedMediaFiles)
    }
    Scaffold { scaffoldPadding ->
        Column (
            modifier = modifier
                .fillMaxWidth()
                .padding(scaffoldPadding.calculateTopPadding())
        ) {
            SearchBar(
                query = query,
                onQueryChange = {
                    query = it
                    onEvent(MediaEvent.QueryChange(query))
                    onEvent(MediaEvent.SearchMedia)
                },
                onSearch = {
                    onEvent(MediaEvent.SearchMedia)
                    active = false
                },
                active = active,
                onActiveChange = {
                    active = it
                },
                placeholder = {
                    Text(text = "Search")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (active)
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .clickable {
                                    if (query.isBlank())
                                        active = false
                                    else
                                        query = ""
                                }
                        )
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                searchItems.forEach { item ->
                    MusicItem(
                        audio = item,
                        onClick = {}
                    )
                }
            }

            LazyColumn {
                items(
                    items = searchItems,
                    key = { it.id }
                ) {
                    MusicItem(
                        audio = it,
                        onClick = { onEvent(MediaEvent.SearchSelectMedia(it, context)) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        state = MediaState(),
        onEvent = {}
    )
}