package com.shpak.quicktimer.ui.timer

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shpak.quicktimer.R
import com.shpak.quicktimer.core.designsystem.component.DismissButton
import com.shpak.quicktimer.core.designsystem.component.StartPauseButton
import com.shpak.quicktimer.core.designsystem.component.TonalActionButton
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme
import com.shpak.quicktimer.ui.permission.NotificationPermissionSheet
import com.shpak.quicktimer.ui.permission.rememberNotificationPermissionRequest
import com.shpak.timer.core.Countdown
import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerSettings
import com.shpak.timer.core.TimerState
import kotlin.time.Duration.Companion.minutes

private val AddTimeMillis = 1.minutes.inWholeMilliseconds

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val requestNotificationPermission = rememberNotificationPermissionRequest()

    TimerScreen(
        uiState = uiState,
        onSetupChange = viewModel::onSetupChange,
        onStart = viewModel::onStart,
        onEvent = viewModel::onEvent,
        onSettingsClick = onSettingsClick
    )

    if (uiState.isNotificationRationaleVisible) {
        NotificationPermissionSheet(
            onConfirm = {
                viewModel.onNotificationRationaleConfirm()
                requestNotificationPermission()
            },
            onDismiss = viewModel::onNotificationRationaleDismiss
        )
    }
}

@Composable
private fun TimerScreen(
    uiState: TimerUiState,
    onSetupChange: ((TimerSetup) -> TimerSetup) -> Unit,
    onStart: () -> Unit,
    onEvent: (TimerEvent) -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        TimerHeader(
            onSettingsClick = onSettingsClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 16.dp, end = 12.dp, bottom = 8.dp)
        )

        TimerContent(
            countdown = uiState.countdown,
            setup = uiState.setup,
            onSetupChange = onSetupChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp)
        )

        TimerControls(
            state = uiState.countdown.state,
            setup = uiState.setup,
            onStart = onStart,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun TimerHeader(
    onSettingsClick: () -> Unit,
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
            onClick = onSettingsClick,
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun TimerContent(
    countdown: Countdown,
    setup: TimerSetup,
    onSetupChange: ((TimerSetup) -> TimerSetup) -> Unit,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = countdown.state is TimerState.Idle,
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        modifier = modifier,
        label = "timerContent"
    ) { isIdle ->
        if (isIdle) {
            TimerSetupPickers(
                setup = setup,
                onSetupChange = onSetupChange,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            TimerCountdownDisplay(
                countdown = countdown,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TimerControls(
    state: TimerState,
    setup: TimerSetup,
    onStart: () -> Unit,
    onEvent: (TimerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val isActive = state !is TimerState.Idle
    val isCancellable = state is TimerState.Running || state is TimerState.Paused

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        if (isCancellable) {
            TextButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.Reject)
                    onEvent(TimerEvent.Stop)
                }
            ) {
                Text(stringResource(R.string.timer_button_cancel))
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isActive) {
                AddMinuteButton(onEvent)
            }
            PrimaryActionButton(state, setup, onStart, onEvent, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AddMinuteButton(
    onEvent: (TimerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    TonalActionButton(
        onClick = {
            onEvent(TimerEvent.AddTime(AddTimeMillis))
        },
        label = stringResource(R.string.timer_button_add_minute),
        modifier = modifier
    )
}

@Composable
private fun PrimaryActionButton(
    state: TimerState,
    setup: TimerSetup,
    onStart: () -> Unit,
    onEvent: (TimerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state is TimerState.Ringing) {
        DismissButton(
            onClick = { onEvent(TimerEvent.Dismiss) },
            label = stringResource(R.string.timer_button_dismiss),
            modifier = modifier
        )
        return
    }

    val isPaused = state is TimerState.Paused
    StartPauseButton(
        isRunning = state is TimerState.Running,
        onStart = {
            if (isPaused) onEvent(TimerEvent.Resume) else onStart()
        },
        onPause = { onEvent(TimerEvent.Pause) },
        startLabel = stringResource(
            if (isPaused) R.string.timer_button_resume else R.string.timer_button_start
        ),
        pauseLabel = stringResource(R.string.timer_button_pause),
        isEnabled = state !is TimerState.Idle || setup.durationMillis > 0L,
        modifier = modifier
    )
}

private val PreviewSetup = TimerSetup(minutes = 5)
private val PreviewSettings = TimerSettings(DismissMode.MANUAL)
private val PreviewRunning =
    Countdown(TimerState.Running(272_000L, PreviewSettings, 300_000L), 272_000L)
private val PreviewPaused =
    Countdown(TimerState.Paused(272_000L, PreviewSettings, 300_000L), 272_000L)
private val PreviewRinging = Countdown(TimerState.Ringing(PreviewSettings), 0L)
private val PreviewIdle = Countdown(TimerState.Idle, 0L)

@Composable
private fun TimerScreenPreview(countdown: Countdown, darkTheme: Boolean = false) =
    QuickTimerTheme(darkTheme = darkTheme) {
        TimerScreen(
            uiState = TimerUiState(countdown = countdown, setup = PreviewSetup),
            onSetupChange = {},
            onStart = {},
            onEvent = {},
            onSettingsClick = {}
        )
    }

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Composable
private fun TimerScreenIdlePreview() = TimerScreenPreview(PreviewIdle)

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Preview(name = "Running narrow", showBackground = true, widthDp = 320, heightDp = 640)
@Preview(
    name = "Running large text",
    showBackground = true,
    widthDp = 400,
    heightDp = 844,
    fontScale = 2f
)
@Composable
private fun TimerScreenRunningPreview() = TimerScreenPreview(PreviewRunning)

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Composable
private fun TimerScreenPausedPreview() = TimerScreenPreview(PreviewPaused)

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Composable
private fun TimerScreenRingingPreview() = TimerScreenPreview(PreviewRinging)

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 844,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun TimerScreenDarkPreview() = TimerScreenPreview(PreviewRunning, darkTheme = true)