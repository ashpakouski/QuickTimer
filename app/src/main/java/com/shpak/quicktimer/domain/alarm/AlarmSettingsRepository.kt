package com.shpak.quicktimer.domain.alarm

import kotlinx.coroutines.flow.Flow

interface AlarmSettingsRepository {
    val settings: Flow<AlarmSettings>
    suspend fun isContinuousPlayback(): Boolean
    suspend fun setContinuousPlayback(isEnabled: Boolean)
    suspend fun getSound(): AlarmSound
    suspend fun setSound(sound: AlarmSound)
}