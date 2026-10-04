package com.shpak.quicktimer.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.core.designsystem.R
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme

@Composable
fun ResetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    FilledTonalIconButton(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
            onClick()
        },
        shapes = IconButtonDefaults.shapes(
            shape = IconButtonDefaults.largeSquareShape,
            pressedShape = IconButtonDefaults.largePressedShape
        ),
        modifier = modifier.size(ResetButtonDefaults.Size)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_replay),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
    }
}

object ResetButtonDefaults {
    val Size: Dp = StartPauseButtonDefaults.Height
}

@Preview(showBackground = true)
@Composable
private fun ResetButtonPreview() {
    QuickTimerTheme {
        ResetButton(
            onClick = {},
            modifier = Modifier.padding(24.dp)
        )
    }
}