package com.shpak.quicktimer.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.core.designsystem.R
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme

@Composable
fun DismissButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null
) {
    val haptics = LocalHapticFeedback.current

    Button(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
            onClick()
        },
        shapes = ButtonDefaults.shapesFor(PlayPauseButtonDefaults.Height),
        contentPadding = PaddingValues(horizontal = 24.dp),
        interactionSource = interactionSource,
        modifier = modifier.height(PlayPauseButtonDefaults.Height)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DismissButtonPreview() {
    QuickTimerTheme {
        DismissButton(
            onClick = {},
            label = "Dismiss",
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        )
    }
}