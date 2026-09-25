package com.shpak.quicktimer.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.core.designsystem.component.NumberPicker
import com.shpak.timer.core.DismissMode
import com.shpak.timer.core.TimerEvent
import com.shpak.timer.core.TimerSettings

@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    // val state by viewModel.state.collectAsState()
    val remainingMillis by viewModel.remainingMillis.collectAsState()
    val setup by viewModel.setup.collectAsState()

    TimerScreen(
        // state = state,
        remainingMillis = remainingMillis,
        setup = setup,
        onSetupChange = viewModel::onSetupChange,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun TimerScreen(
    // state: TimerState,
    remainingMillis: Long,
    setup: TimerSetup,
    onSetupChange: ((TimerSetup) -> TimerSetup) -> Unit,
    onEvent: (TimerEvent) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
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

            Text(
                text = "$remainingMillis"
            )

//            Text(
//                text = "$state"
//            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onEvent(
                            TimerEvent.Start(
                                durationMillis = setup.durationMillis,
                                settings = TimerSettings(
                                    dismissMode = DismissMode.AUTOMATIC
                                )
                            )
                        )
                    },
                    enabled = setup.durationMillis > 0
                ) {
                    Text("Start auto")
                }

                Button(
                    onClick = {
                        onEvent(
                            TimerEvent.Start(
                                durationMillis = setup.durationMillis,
                                settings = TimerSettings(
                                    dismissMode = DismissMode.MANUAL
                                )
                            )
                        )
                    },
                    enabled = setup.durationMillis > 0
                ) {
                    Text("Start manual")
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onEvent(TimerEvent.Pause)
                    },
                    enabled = true // state is TimerState.Running
                ) {
                    Text("Pause")
                }

                Button(
                    onClick = {
                        onEvent(TimerEvent.Resume)
                    },
                    enabled = true // state is TimerState.Paused
                ) {
                    Text("Resume")
                }

                Button(
                    onClick = {
                        onEvent(TimerEvent.Stop)
                    },
                    enabled = true // state !is TimerState.Idle
                ) {
                    Text("Stop")
                }
            }
        }
    }
}