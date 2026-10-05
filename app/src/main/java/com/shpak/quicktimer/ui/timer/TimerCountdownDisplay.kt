package com.shpak.quicktimer.ui.timer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.R
import com.shpak.quicktimer.core.designsystem.theme.TimerTextStyles
import com.shpak.quicktimer.util.toTimerDisplay
import com.shpak.timer.core.Countdown
import com.shpak.timer.core.TimerState

private const val ProgressTickMillis = 100
private val RingStrokeWidth = 8.dp

@Composable
internal fun TimerCountdownDisplay(
    countdown: Countdown,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.aspectRatio(1f)
        ) {
            when (val state = countdown.state) {
                is TimerState.Running -> ProgressRing(countdown, state.totalMillis)
                is TimerState.Paused -> ProgressRing(countdown, state.totalMillis)
                else -> Unit
            }
            RemainingTime(countdown)
        }
    }
}

@Composable
private fun ProgressRing(countdown: Countdown, totalMillis: Long) {
    val progress by animateFloatAsState(
        targetValue = countdown.remainingMillis.toFloat() / totalMillis,
        animationSpec = if (countdown.state is TimerState.Running) {
            tween(ProgressTickMillis, easing = LinearEasing)
        } else {
            snap()
        },
        label = "remainingProgress"
    )

    CircularProgressIndicator(
        progress = { progress },
        strokeWidth = RingStrokeWidth,
        strokeCap = StrokeCap.Round,
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun RemainingTime(countdown: Countdown) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = countdown.remainingMillis.toTimerDisplay(),
            style = TimerTextStyles.countdown,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (countdown.state is TimerState.Ringing) {
            Text(
                text = stringResource(R.string.timer_time_up),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
