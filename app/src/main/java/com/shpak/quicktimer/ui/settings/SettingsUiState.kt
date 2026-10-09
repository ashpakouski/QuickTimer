package com.shpak.quicktimer.ui.settings

import com.shpak.quicktimer.domain.alarm.AlarmSound

data class SettingsUiState(
    val selectedSound: AlarmSound? = null,
    val previewingSound: AlarmSound? = null
)