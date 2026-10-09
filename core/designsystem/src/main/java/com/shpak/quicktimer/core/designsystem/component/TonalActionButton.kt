package com.shpak.quicktimer.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme

@Composable
fun TonalActionButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    FilledTonalButton(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
            onClick()
        },
        shapes = ButtonDefaults.shapes(
            shape = IconButtonDefaults.largeSquareShape,
            pressedShape = IconButtonDefaults.largePressedShape
        ),
        contentPadding = ButtonDefaults.contentPaddingFor(StartPauseButtonDefaults.Height),
        modifier = modifier.height(StartPauseButtonDefaults.Height)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TonalActionButtonPreview() {
    QuickTimerTheme {
        TonalActionButton(
            onClick = {},
            label = "+1 min",
            modifier = Modifier.padding(24.dp)
        )
    }
}