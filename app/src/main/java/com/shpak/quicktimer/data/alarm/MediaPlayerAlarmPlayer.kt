package com.shpak.quicktimer.data.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import com.shpak.quicktimer.domain.alarm.AlarmPlayer
import com.shpak.quicktimer.domain.alarm.AlarmSound
import com.shpak.quicktimer.domain.alarm.SoundPreviewPlayer

class MediaPlayerAlarmPlayer(context: Context) : AlarmPlayer, SoundPreviewPlayer {
    private val context = context.applicationContext
    private val audioManager = context.getSystemService(AudioManager::class.java)

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val focusRequest = AudioFocusRequest
        .Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        .setAudioAttributes(audioAttributes)
        .build()

    private var player: MediaPlayer? = null

    override fun play(sound: AlarmSound, isLooping: Boolean) {
        start(sound, isLooping, onComplete = {})
    }

    override fun play(sound: AlarmSound, onComplete: () -> Unit) {
        start(sound, isLooping = false, onComplete = onComplete)
    }

    private fun start(sound: AlarmSound, isLooping: Boolean, onComplete: () -> Unit) {
        stop()

        val player = MediaPlayer.create(
            context, sound.resourceId, audioAttributes, AudioManager.AUDIO_SESSION_ID_GENERATE
        ) ?: return

        player.isLooping = isLooping
        player.setOnCompletionListener {
            stop()
            onComplete()
        }
        player.setOnErrorListener { _, _, _ ->
            stop()
            onComplete()
            true
        }

        audioManager?.requestAudioFocus(focusRequest)
        player.start()

        this.player = player
    }

    override fun stop() {
        val player = player ?: return
        this.player = null

        player.release()
        audioManager?.abandonAudioFocusRequest(focusRequest)
    }
}