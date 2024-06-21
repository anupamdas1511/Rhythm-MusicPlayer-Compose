package com.anupam.musicplayer.viewmodels

sealed class PermissionEvent {
    data class OnPermissionResult(val permission: String, val isGranted: Boolean): PermissionEvent()
    data object DismissPermissionDialog: PermissionEvent()
}