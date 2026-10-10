package com.shpak.quicktimer.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.R
import com.shpak.quicktimer.core.designsystem.component.NumberPicker

@Composable
internal fun TimerSetupPickers(
    setup: TimerSetup,
    onSetupChange: ((TimerSetup) -> TimerSetup) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TimeUnitPicker(
            label = stringResource(R.string.timer_settings_label_hours),
            value = setup.hours,
            range = TimerSetup.HoursRange,
            onValueChange = { hours ->
                onSetupChange { current ->
                    current.copy(hours = hours)
                }
            },
            modifier = Modifier.weight(1f)
        )

        TimeUnitPicker(
            label = stringResource(R.string.timer_settings_label_minutes),
            value = setup.minutes,
            range = TimerSetup.MinutesRange,
            onValueChange = { minutes ->
                onSetupChange { current ->
                    current.copy(minutes = minutes)
                }
            },
            modifier = Modifier.weight(1f)
        )

        TimeUnitPicker(
            label = stringResource(R.string.timer_settings_label_seconds),
            value = setup.seconds,
            range = TimerSetup.SecondsRange,
            onValueChange = { seconds ->
                onSetupChange { current ->
                    current.copy(seconds = seconds)
                }
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TimeUnitPicker(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        NumberPicker(
            value = value,
            onValueChange = onValueChange,
            range = range,
            modifier = Modifier.fillMaxWidth()
        )
    }
}