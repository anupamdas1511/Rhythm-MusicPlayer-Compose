package com.anupam.musicplayer.presentations.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Divider()
                Text(
                    text = if (isPermanentlyDeclined) "Grant Permission" else "OK",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = "Permission required")
        },
        text = {
            Text(text = permissionTextProvider.getDescription(isPermanentlyDeclined))
        },
        modifier = modifier
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class StoragePermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you have permanently declined Storage Permission. " +
                    "You can go to the app settings to grant it"
        } else {
            "This app requires the permission for accessing media files"
        }
    }
}

class NotificationPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you have permanently declined Notification Permission. " +
                    "You can go to the app settings to grant it"
        } else {
            "This app requires the permission for showing Notification Media Playback"
        }
    }
}

class ForegroundServicePermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you have permanently declined Foreground Service Permission. You can go to the app settings to grant it."
        } else {
            "This app requires the permission for running a foreground service."
        }
    }
}