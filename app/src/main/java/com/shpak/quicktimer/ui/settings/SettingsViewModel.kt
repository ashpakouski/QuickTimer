package com.shpak.quicktimer.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.domain.alarm.AlarmSettings
import com.shpak.quicktimer.domain.alarm.AlarmSettingsRepository
import com.shpak.quicktimer.domain.alarm.AlarmSound
import com.shpak.quicktimer.domain.alarm.SoundPreviewPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val alarmSettings: AlarmSettingsRepository = Hub.get<AlarmSettingsRepository>(),
    private val previewPlayer: SoundPreviewPlayer = Hub.get<SoundPreviewPlayer>()
) : ViewModel() {
    private val _previewingSound = MutableStateFlow<AlarmSound?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        alarmSettings.settings,
        _previewingSound
    ) { settings, previewingSound ->
        settings.toUiState(previewingSound)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun onSoundSelect(sound: AlarmSound) {
        viewModelScope.launch {
            alarmSettings.setSound(sound)
        }
    }

    fun onPreviewToggle(sound: AlarmSound) {
        val isSameSound = _previewingSound.value == sound
        stopPreview()

        if (isSameSound) {
            return
        }

        _previewingSound.value = sound

        previewPlayer.play(sound) {
            _previewingSound.value = null
        }
    }

    fun stopPreview() {
        previewPlayer.stop()
        _previewingSound.value = null
    }

    override fun onCleared() {
        stopPreview()
    }
}

private fun AlarmSettings.toUiState(previewingSound: AlarmSound?) = SettingsUiState(
    selectedSound = sound,
    previewingSound = previewingSound
)