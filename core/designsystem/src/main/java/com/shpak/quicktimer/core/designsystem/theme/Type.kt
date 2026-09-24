package com.shpak.quicktimer.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.shpak.quicktimer.core.designsystem.R

private val TimerFontFamily = FontFamily(
    Font(R.font.google_sans_flex_regular, FontWeight.Normal),
    Font(R.font.google_sans_flex_medium, FontWeight.Medium)
)

private fun uiText(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
    tracking: TextUnit = 0.sp
) = TextStyle(
    fontFamily = TimerFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking,
    fontSynthesis = FontSynthesis.None,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false
    )
)

val QuickTimerTypography = Typography(
    displayLarge = uiText(57, 64, tracking = (-0.25).sp),
    displayMedium = uiText(45, 52),
    displaySmall = uiText(36, 44),
    headlineLarge = uiText(32, 40, tracking = (-0.5).sp),
    headlineMedium = uiText(28, 36, tracking = (-0.25).sp),
    headlineSmall = uiText(24, 32),
    titleLarge = uiText(22, 28),
    titleMedium = uiText(16, 24, FontWeight.Medium),
    titleSmall = uiText(14, 20, FontWeight.Medium),
    bodyLarge = uiText(16, 24),
    bodyMedium = uiText(14, 20),
    bodySmall = uiText(12, 16),
    labelLarge = uiText(14, 20, FontWeight.Medium),
    labelMedium = uiText(12, 16, FontWeight.Medium),
    labelSmall = uiText(11, 16, FontWeight.Medium)
)

private fun numeralText(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
    tracking: TextUnit = 0.sp,
) = uiText(size, lineHeight, weight, tracking).copy(
    fontFeatureSettings = "'tnum' 1, 'lnum' 1",
    textDirection = TextDirection.Ltr,
)

object TimerTextStyles {
    val wheelSelected = numeralText(44, 52, FontWeight.Medium, (-0.03).em)
    val wheelUnselected = numeralText(32, 40, tracking = (-0.02).em)
}