package com.shpak.quicktimer.ui.permission

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.R
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPermissionSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun hideThen(action: () -> Unit) {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            action()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = modifier
    ) {
        NotificationPermissionContent(
            onConfirm = {
                hideThen(onConfirm)
            },
            onDismiss = {
                hideThen(onDismiss)
            },
            shouldShowCloseButton = false
        )
    }
}

@Composable
fun NotificationPermissionContent(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    shouldShowCloseButton: Boolean,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = modifier
    ) {
        Header(
            onClickClose = onDismiss.takeIf { shouldShowCloseButton },
            modifier = Modifier
                .padding(
                    start = 24.dp,
                    top = 16.dp,
                    end = 8.dp,
                    bottom = 16.dp
                )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                text = stringResource(R.string.notification_permission_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                TextButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_button_not_now)
                    )
                }

                Button(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        onConfirm()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_button_allow)
                    )
                }
            }
        }
    }

//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
//    ) {
//
//
//    }
}

@Composable
private fun Header(
    onClickClose: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.notification_permission_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (onClickClose != null) {
            IconButton(
                onClick = onClickClose,
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun NotificationPermissionContentPreview(darkTheme: Boolean = false) =
    QuickTimerTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
            NotificationPermissionContent(
                onConfirm = {},
                onDismiss = {},
                shouldShowCloseButton = true
            )
        }
    }

@Preview(widthDp = 400)
@Composable
private fun NotificationPermissionLightPreview() = NotificationPermissionContentPreview()

@Preview(widthDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NotificationPermissionDarkPreview() =
    NotificationPermissionContentPreview(darkTheme = true)