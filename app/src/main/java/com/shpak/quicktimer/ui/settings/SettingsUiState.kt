package com.shpak.quicktimer.ui.settings

import com.shpak.quicktimer.domain.alarm.AlarmSound

data class SettingsUiState(
    val selectedSound: AlarmSound = AlarmSound.Default,
    val previewingSound: AlarmSound? = null
)