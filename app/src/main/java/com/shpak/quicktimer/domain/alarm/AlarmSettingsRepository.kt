package com.shpak.quicktimer.domain.alarm

import kotlinx.coroutines.flow.Flow

interface AlarmSettingsRepository {
    val settings: Flow<AlarmSettings>
    suspend fun getSound(): AlarmSound
    suspend fun setSound(sound: AlarmSound)
}