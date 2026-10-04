package com.shpak.quicktimer.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.R
import com.shpak.quicktimer.core.designsystem.component.AddTimeButton
import com.shpak.quicktimer.core.designsystem.component.DismissButton
import com.shpak.quicktimer.core.designsystem.component.NumberPicker
import com.shpak.quicktimer.core.designsystem.component.StartPauseButton
import com.shpak.quicktimer.core.designsystem.component.ResetButton
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme
import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerSettings
import com.shpak.timer.core.TimerState

@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    val state by viewModel.state.collectAsState()
    val setup by viewModel.setup.collectAsState()

    TimerScreen(
        state = state,
        setup = setup,
        onSetupChange = viewModel::onSetupChange,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun TimerScreen(
    state: TimerState,
    setup: TimerSetup,
    onSetupChange: ((TimerSetup) -> TimerSetup) -> Unit,
    onEvent: (TimerEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (state is TimerState.Idle) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    NumberPicker(
                        value = setup.hours,
                        onValueChange = { hours ->
                            onSetupChange { setup ->
                                setup.copy(hours = hours)
                            }
                        },
                        range = TimerSetup.HoursRange,
                        modifier = Modifier.width(100.dp)
                    )

                    NumberPicker(
                        value = setup.minutes,
                        onValueChange = { minutes ->
                            onSetupChange { setup ->
                                setup.copy(minutes = minutes)
                            }
                        },
                        range = TimerSetup.MinutesRange,
                        modifier = Modifier.width(100.dp)
                    )

                    NumberPicker(
                        value = setup.seconds,
                        onValueChange = { seconds ->
                            onSetupChange { setup ->
                                setup.copy(seconds = seconds)
                            }
                        },
                        range = TimerSetup.SecondsRange,
                        modifier = Modifier.width(100.dp)
                    )
                }
            }
        }

        Text(
            text = "$state",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )

        TimerControls(
            state = state,
            isStartEnabled = setup.durationMillis > 0,
            onStart = {
                onEvent(
                    TimerEvent.Start(
                        durationMillis = setup.durationMillis,
                        settings = TimerSettings(dismissMode = DismissMode.MANUAL)
                    )
                )
            },
            onPause = { onEvent(TimerEvent.Pause) },
            onResume = { onEvent(TimerEvent.Resume) },
            onReset = { onEvent(TimerEvent.Stop) },
            onDismiss = { onEvent(TimerEvent.Dismiss) },
            onAddMinute = { },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun TimerControls(
    state: TimerState,
    isStartEnabled: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    onAddMinute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        when (state) {
            is TimerState.Paused -> ResetButton(
                onClick = onReset
            )

            is TimerState.Ringing -> AddTimeButton(
                onClick = onAddMinute,
                label = stringResource(R.string.timer_button_add_minute)
            )

            else -> Unit
        }

        if (state is TimerState.Ringing) {
            DismissButton(
                onClick = onDismiss,
                label = stringResource(R.string.timer_button_dismiss),
                modifier = Modifier.weight(1f)
            )
        } else {
            val isPaused = state is TimerState.Paused

            StartPauseButton(
                isRunning = state is TimerState.Running,
                onStart = if (isPaused) onResume else onStart,
                onPause = onPause,
                startLabel = stringResource(
                    if (isPaused) {
                        R.string.timer_button_resume
                    } else {
                        R.string.timer_button_start
                    }
                ),
                pauseLabel = stringResource(R.string.timer_button_pause),
                isEnabled = isPaused || state is TimerState.Running || isStartEnabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Composable
private fun TimerScreenPreview() {
    QuickTimerTheme {
        TimerScreen(
            state = TimerState.Idle,
            setup = TimerSetup(minutes = 5),
            onSetupChange = {},
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Composable
private fun TimerScreenRunningPreview() {
    QuickTimerTheme {
        TimerScreen(
            state = TimerState.Running(
                endTimeMillis = 0L,
                settings = TimerSettings(dismissMode = DismissMode.MANUAL)
            ),
            setup = TimerSetup(minutes = 5),
            onSetupChange = {},
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Composable
private fun TimerScreenPausedPreview() {
    QuickTimerTheme {
        TimerScreen(
            state = TimerState.Paused(
                remainingMillis = 60_000L,
                settings = TimerSettings(dismissMode = DismissMode.MANUAL)
            ),
            setup = TimerSetup(minutes = 5),
            onSetupChange = {},
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Composable
private fun TimerScreenRingingPreview() {
    QuickTimerTheme {
        TimerScreen(
            state = TimerState.Ringing(
                settings = TimerSettings(dismissMode = DismissMode.MANUAL)
            ),
            setup = TimerSetup(minutes = 5),
            onSetupChange = {},
            onEvent = {}
        )
    }
}