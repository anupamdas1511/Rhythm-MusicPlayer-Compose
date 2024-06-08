package com.anupam.musicplayer.presentations.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class SearchState(initialSearchModeEnabled: Boolean = false) {
    var isSearchModeEnabled by mutableStateOf(initialSearchModeEnabled)

    fun enableSearchMode() {
        isSearchModeEnabled = true
    }
    fun disableSearchMode() {
        isSearchModeEnabled = false
    }
}

object SearchModeSaver: Saver<SearchState, Boolean> {
    override fun restore(value: Boolean): SearchState? {
        return SearchState(value)
    }

    override fun SaverScope.save(value: SearchState): Boolean? {
        return value.isSearchModeEnabled
    }
}

@Composable
fun rememberSearchState(): SearchState {
    return rememberSaveable(saver = SearchModeSaver) {
        SearchState()
    }
}