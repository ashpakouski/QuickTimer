package com.shpak.quicktimer.ui.timer

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.R
import com.shpak.quicktimer.core.designsystem.component.StartPauseButton
import com.shpak.quicktimer.core.designsystem.component.TonalActionButton
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme

@Composable
fun TimerSetupUi(
    setup: TimerSetup,
    isVolumeLow: Boolean,
    onSetupChange: ((TimerSetup) -> TimerSetup) -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        TimerHeader(
            onClickClose = onCancel,
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
            TimerSetupPickers(
                setup = setup,
                onSetupChange = onSetupChange,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = isVolumeLow) {
                VolumeWarning()
            }

            TimerSetupControls(
                isStartEnabled = setup.durationMillis > 0L,
                onStart = onStart,
                onCancel = onCancel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun VolumeWarning(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_warning),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )

        Text(
            text = stringResource(R.string.warning_volume_low),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun TimerSetupControls(
    isStartEnabled: Boolean,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        TonalActionButton(
            onClick = onCancel,
            label = stringResource(R.string.timer_settings_button_cancel)
        )

        val startLabel = stringResource(R.string.timer_settings_button_start)
        StartPauseButton(
            isRunning = false,
            onStart = onStart,
            onPause = {},
            startLabel = startLabel,
            pauseLabel = startLabel,
            isEnabled = isStartEnabled,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TimerHeader(
    onClickClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

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

@Composable
private fun TimerSetupSheetPreview(isVolumeLow: Boolean, darkTheme: Boolean = false) =
    QuickTimerTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
            TimerSetupUi(
                setup = TimerSetup(minutes = 5),
                isVolumeLow = isVolumeLow,
                onSetupChange = {},
                onStart = {},
                onCancel = {}
            )
        }
    }

@Preview(widthDp = 400)
@Composable
private fun TimerSetupSheetDefaultPreview() = TimerSetupSheetPreview(isVolumeLow = false)

@Preview(widthDp = 400)
@Composable
private fun TimerSetupSheetVolumeLowPreview() = TimerSetupSheetPreview(isVolumeLow = true)

@Preview(widthDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimerSetupSheetDarkPreview() =
    TimerSetupSheetPreview(isVolumeLow = false, darkTheme = true)