package com.shpak.quicktimer.core.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.core.designsystem.icon.TimerIcons
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme

enum class PlayButtonMode(
    internal val icon: ImageVector
) {
    Start(TimerIcons.Play),
    Pause(TimerIcons.Pause),
    Resume(TimerIcons.Play),
    Dismiss(TimerIcons.Check)
}

@Composable
fun PlayButton(
    mode: PlayButtonMode,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    shapes: ToggleButtonShapes = PlayButtonDefaults.shapes(),
    colors: ToggleButtonColors = PlayButtonDefaults.colors()
) {
    val interactionSource = remember(::MutableInteractionSource)
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) {
            PlayButtonDefaults.PressedScale
        } else {
            1f
        },
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "playButtonScale"
    )

    ToggleButton(
        checked = mode == PlayButtonMode.Pause,
        onCheckedChange = { onClick() },
        enabled = isEnabled,
        shapes = shapes,
        colors = colors,
        contentPadding = PaddingValues(horizontal = 24.dp),
        interactionSource = interactionSource,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(PlayButtonDefaults.Height)
    ) {
        AnimatedContent(
            targetState = mode to label,
            transitionSpec = {
                (fadeIn() + scaleIn(initialScale = 0.8f)) togetherWith fadeOut()
            },
            contentAlignment = Alignment.Center,
            label = "playButtonContent"
        ) { (targetMode, targetLabel) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = targetMode.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = targetLabel,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1
                )
            }
        }
    }
}

object PlayButtonDefaults {
    val Height: Dp = 80.dp
    val CornerRadius: Dp = 40.dp
    val RunningCornerRadius: Dp = 27.dp
    val PressedCornerRadius: Dp = 18.dp

    const val PressedScale: Float = 0.96f

    fun shapes(
        cornerRadius: Dp = CornerRadius,
        runningCornerRadius: Dp = RunningCornerRadius,
        pressedCornerRadius: Dp = PressedCornerRadius
    ): ToggleButtonShapes = ToggleButtonShapes(
        shape = RoundedCornerShape(cornerRadius),
        pressedShape = RoundedCornerShape(pressedCornerRadius),
        checkedShape = RoundedCornerShape(runningCornerRadius)
    )

    @Composable
    fun colors(): ToggleButtonColors = ToggleButtonDefaults.colors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        checkedContainerColor = MaterialTheme.colorScheme.primary,
        checkedContentColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Preview(name = "Play button modes", showBackground = true, widthDp = 360)
@Composable
private fun PlayButtonModesPreview() {
    QuickTimerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            PlayButtonMode.entries.forEach { mode ->
                PlayButton(
                    mode = mode,
                    label = mode.name,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
            PlayButton(
                mode = PlayButtonMode.Start,
                label = "Start",
                onClick = {},
                isEnabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Corner sizes", showBackground = true, widthDp = 360)
@Composable
private fun PlayButtonCornersPreview() {
    QuickTimerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            listOf(0.dp, 8.dp, 18.dp, 27.dp, 40.dp).forEach { radius ->
                PlayButton(
                    mode = PlayButtonMode.Start,
                    label = "${radius.value.toInt()}dp",
                    onClick = {},
                    shapes = PlayButtonDefaults.shapes(cornerRadius = radius),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(
    name = "Play button dark",
    showBackground = true,
    backgroundColor = 0xFF13150E,
    widthDp = 360,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PlayButtonDarkPreview() {
    QuickTimerTheme(darkTheme = true) {
        PlayButton(
            mode = PlayButtonMode.Pause,
            label = "Pause",
            onClick = {},
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        )
    }
}