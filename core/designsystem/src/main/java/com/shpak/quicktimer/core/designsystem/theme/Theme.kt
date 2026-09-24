package com.shpak.quicktimer.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

@Composable
fun QuickTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    shapes: Shapes = Shapes(),
    content: @Composable () -> Unit
) {
    val useDynamicColor = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        useDynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        useDynamicColor -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val baseTimerColors = if (darkTheme) DarkTimerColors else LightTimerColors
    val timerColors = if (useDynamicColor) {
        baseTimerColors.copy(
            progressTrack = colorScheme.surfaceContainerHighest,
            keypadContainer = colorScheme.surfaceContainer,
            settingsScrim = colorScheme.scrim.copy(
                alpha = baseTimerColors.settingsScrim.alpha
            )
        )
    } else {
        baseTimerColors
    }

    CompositionLocalProvider(LocalTimerColors provides timerColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = QuickTimerTypography,
            shapes = shapes,
            content = content
        )
    }
}