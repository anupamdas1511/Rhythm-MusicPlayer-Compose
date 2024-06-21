package com.anupam.musicplayer.presentations.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.anupam.musicplayer.NavigationItem
import com.anupam.musicplayer.presentations.components.ForegroundServicePermissionTextProvider
import com.anupam.musicplayer.presentations.components.NotificationPermissionTextProvider
import com.anupam.musicplayer.presentations.components.PermissionDialog
import com.anupam.musicplayer.presentations.components.StoragePermissionTextProvider
import com.anupam.musicplayer.viewmodels.PermissionEvent

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PermissionScreen(
    onPermissionEvent: (PermissionEvent) -> Unit,
    navController: NavController,
    dialogQueue: List<String>,
    permissionsToRequest: Array<String>
) {
    val context = LocalContext.current

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            if (!perms.all { it.value }) {
                perms.keys.forEach { permission ->
                    onPermissionEvent(
                        PermissionEvent.OnPermissionResult(
                            permission = permission,
                            isGranted = perms[permission] == true
                        )
                    )
                }
            }
//            if (perms.all { it.value }) {
//                navController.navigate(NavigationItem.Dashboard.route)
//                Log.d("Kuch toh debug", "Navigated")
//            }
        }
    )
    LaunchedEffect(Unit) {
        val allPermissionsGranted = checkIfAllPermissionsGranted(context, permissionsToRequest)
        Log.d("kuch toh debug", allPermissionsGranted.toString())
        if (!allPermissionsGranted) {
            multiplePermissionResultLauncher.launch(
                permissionsToRequest
            )
        }
    }
    if (checkIfAllPermissionsGranted(context, permissionsToRequest)) {
        navController.navigate(NavigationItem.Dashboard.route)
    }
//    if (!allPermissionsGranted)
    dialogQueue
        .reversed()
        .forEach { permission ->
            if (!checkIfPermissionGranted(context, permission))
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        Manifest.permission.POST_NOTIFICATIONS -> {
                            NotificationPermissionTextProvider()
                        }

                        Manifest.permission.FOREGROUND_SERVICE -> {
                            ForegroundServicePermissionTextProvider()
                        }

                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            StoragePermissionTextProvider()
                        }

                        Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                            StoragePermissionTextProvider()
                        }

                        else -> return@forEach
                    },
                    isPermanentlyDeclined = ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        permission
                    ),
                    onDismiss = { onPermissionEvent(PermissionEvent.DismissPermissionDialog) },
                    onOkClick = {
                        onPermissionEvent(PermissionEvent.DismissPermissionDialog)
                        multiplePermissionResultLauncher.launch(
                            arrayOf(permission)
                        )
                    },
                    onGoToAppSettingsClick = context::openAppSettings
                )
        }
}

fun checkIfAllPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
    Log.d("PermissionScreen", "Permission Size: ${permissions.size}")
    return permissions.all { permission ->
        val isGranted = checkIfPermissionGranted(context, permission)
        Log.d("PermissionScreen", "Permission: $permission, Granted: $isGranted")
        isGranted
    }
}

fun checkIfPermissionGranted(context: Context, permission: String): Boolean {
    return when (permission) {
        Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        else -> ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}