package com.shpak.quicktimer.ui.permission

import android.Manifest
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.domain.analytics.AnalyticsEvent
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger
import com.shpak.quicktimer.util.redirectToNotificationSettings

@Composable
fun rememberNotificationPermissionRequest(): () -> Unit {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val analytics = remember { Hub.get<AnalyticsLogger>() }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        analytics.log(AnalyticsEvent.NotificationPermissionResult(isGranted))

        val isPermanentlyDenied = !isGranted && activity != null &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, Manifest.permission.POST_NOTIFICATIONS
                )

        if (isPermanentlyDenied) {
            analytics.log(AnalyticsEvent.NotificationSettingsOpen)
            redirectToNotificationSettings(context)
        }
    }

    return {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            analytics.log(AnalyticsEvent.NotificationSettingsOpen)
            redirectToNotificationSettings(context)
        }
    }
}