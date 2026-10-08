package com.shpak.quicktimer.data.alarm

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.shpak.quicktimer.domain.alarm.AlarmSettings
import com.shpak.quicktimer.domain.alarm.AlarmSettingsRepository
import com.shpak.quicktimer.domain.alarm.AlarmSound
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.alarmDataStore: DataStore<Preferences> by preferencesDataStore("alarm_preferences")
private val KEY_SOUND_ID = stringPreferencesKey("sound_id")

class DefaultAlarmSettingsRepository(context: Context) : AlarmSettingsRepository {
    private val dataStore = context.applicationContext.alarmDataStore

    override val settings: Flow<AlarmSettings> = dataStore.data.map { preferences ->
        AlarmSettings(
            sound = preferences.sound
        )
    }

    override suspend fun getSound(): AlarmSound = dataStore.data.first().sound

    override suspend fun setSound(sound: AlarmSound) {
        dataStore.edit { preferences ->
            preferences[KEY_SOUND_ID] = sound.id
        }
    }

    private val Preferences.sound: AlarmSound
        get() = AlarmSound.fromId(this[KEY_SOUND_ID])
}