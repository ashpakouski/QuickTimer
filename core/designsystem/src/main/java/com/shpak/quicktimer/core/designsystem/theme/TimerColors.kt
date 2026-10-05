package com.shpak.quicktimer.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Timer-specific semantic colors, supplied by [QuickTimerTheme]. */
@Immutable
data class TimerColors(
    val alarmContainer: Color,
    val onAlarmContainer: Color,
    val progressTrack: Color,
    val keypadContainer: Color,
    /** Includes opacity. Pass directly to ModalBottomSheet's scrimColor. */
    val settingsScrim: Color,
)

val LightTimerColors = TimerColors(
    alarmContainer = LightColorScheme.tertiaryContainer,
    onAlarmContainer = LightColorScheme.onTertiaryContainer,
    progressTrack = Color(0xFFDFE5F3),
    keypadContainer = Color(0xFFF0F2F9),
    // CSS #11172855 becomes Android's ARGB #55111728.
    settingsScrim = Color(0x55111728),
)

val DarkTimerColors = TimerColors(
    alarmContainer = DarkColorScheme.tertiary,
    onAlarmContainer = DarkColorScheme.onTertiary,
    progressTrack = Color(0xFF2B3144),
    keypadContainer = Color(0xFF232733),
    // CSS #0008 expands to #00000088; Android places the alpha first.
    settingsScrim = Color(0x88000000),
)

internal val LocalTimerColors = staticCompositionLocalOf { LightTimerColors }

/** Access inside QuickTimerTheme, for example MaterialTheme.timerColors.alarmContainer. */
val MaterialTheme.timerColors: TimerColors
    @Composable
    @ReadOnlyComposable
    get() = LocalTimerColors.current
