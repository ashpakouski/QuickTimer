package com.shpak.quicktimer.core.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.core.designsystem.R
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme

@Composable
fun StartPauseButton(
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    startLabel: String,
    pauseLabel: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    shapes: ToggleButtonShapes = StartPauseButtonDefaults.shapes(),
    colors: ToggleButtonColors = StartPauseButtonDefaults.colors()
) {
    val haptics = LocalHapticFeedback.current
    val motionScheme = MaterialTheme.motionScheme

    ToggleButton(
        checked = isRunning,
        onCheckedChange = { start ->
            if (start) {
                haptics.performHapticFeedback(HapticFeedbackType.ToggleOn)
                onStart()
            } else {
                haptics.performHapticFeedback(HapticFeedbackType.ToggleOff)
                onPause()
            }
        },
        enabled = isEnabled,
        shapes = shapes,
        colors = colors,
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = modifier.height(StartPauseButtonDefaults.Height)
    ) {
        AnimatedContent(
            targetState = if (isRunning) {
                R.drawable.ic_pause to pauseLabel
            } else {
                R.drawable.ic_play_arrow to startLabel
            },
            transitionSpec = {
                (fadeIn(motionScheme.fastEffectsSpec()) +
                        scaleIn(motionScheme.fastSpatialSpec(), initialScale = 0.8f))
                    .togetherWith(fadeOut(motionScheme.fastEffectsSpec()))
                    .using(
                        SizeTransform(
                            clip = false,
                            sizeAnimationSpec = { _, _ -> motionScheme.defaultSpatialSpec() }
                        )
                    )
            },
            contentAlignment = Alignment.Center,
            label = "startPauseContent"
        ) { (icon, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

object StartPauseButtonDefaults {
    val Height: Dp = 80.dp

    @Composable
    fun shapes(): ToggleButtonShapes = ToggleButtonDefaults.shapesFor(Height)

    @Composable
    fun colors(): ToggleButtonColors = ToggleButtonDefaults.colors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        checkedContainerColor = MaterialTheme.colorScheme.primary,
        checkedContentColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Preview(name = "Start/pause states", showBackground = true, widthDp = 360)
@Composable
private fun StartPauseButtonStatesPreview() {
    QuickTimerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            listOf(false, true).forEach { isRunning ->
                StartPauseButton(
                    isRunning = isRunning,
                    onStart = {},
                    onPause = {},
                    startLabel = "Start",
                    pauseLabel = "Pause",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            StartPauseButton(
                isRunning = false,
                onStart = {},
                onPause = {},
                startLabel = "Start",
                pauseLabel = "Pause",
                isEnabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(
    name = "Start/pause dark",
    showBackground = true,
    backgroundColor = 0xFF13150E,
    widthDp = 360,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun StartPauseButtonDarkPreview() {
    QuickTimerTheme(darkTheme = true) {
        StartPauseButton(
            isRunning = true,
            onStart = {},
            onPause = {},
            startLabel = "Start",
            pauseLabel = "Pause",
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        )
    }
}