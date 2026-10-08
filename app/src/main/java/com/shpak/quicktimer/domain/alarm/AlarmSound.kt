package com.shpak.quicktimer.domain.alarm

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.shpak.quicktimer.R

enum class AlarmSound(
    val id: String,
    @param:RawRes val resourceId: Int,
    @param:StringRes val labelId: Int
) {
    DOUBLE_PING("double_ping", R.raw.double_ping, R.string.sound_double_ping),
    CHIME("chime", R.raw.chime, R.string.sound_chime),
    MARIMBA("marimba", R.raw.marimba, R.string.sound_marimba),
    BELL("bell", R.raw.bell, R.string.sound_bell),
    DIGITAL("digital", R.raw.digital, R.string.sound_digital);

    companion object {
        val Default = DOUBLE_PING

        fun fromId(id: String?): AlarmSound = entries.firstOrNull { sound -> sound.id == id } ?: Default
    }
}