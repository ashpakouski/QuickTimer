package com.shpak.quicktimer.domain.alarm

data class AlarmSettings(
    val isContinuousPlayback: Boolean = true,
    val sound: AlarmSound = AlarmSound.Default
)