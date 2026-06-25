package com.shpak.quicktimer.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.shpak.quicktimer.R
import com.shpak.quicktimer.data.timer.TimerCompletionScheduler
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.domain.timer.TimerRepository
import com.shpak.quicktimer.domain.timer.TimerState
import com.shpak.quicktimer.presentation.QuickTimerNotificationController.Companion.ACTION_CANCEL
import com.shpak.quicktimer.presentation.QuickTimerNotificationController.Companion.ACTION_PAUSE
import com.shpak.quicktimer.presentation.QuickTimerNotificationController.Companion.ACTION_RESUME
import com.shpak.quicktimer.presentation.QuickTimerNotificationController.Companion.NOTIFICATION_ID
import com.shpak.quicktimer.util.Debouncer
import com.shpak.quicktimer.util.lazyTryOrNull
import com.shpak.quicktimer.util.playSound
import kotlinx.coroutines.launch

class TimerService : LifecycleService() {
    companion object {
        private const val KEY_TIME_MILLIS = "time_millis"

        fun start(context: Context, timeMillis: Long) {
            val startIntent = Intent(context, TimerService::class.java)
            startIntent.putExtra(KEY_TIME_MILLIS, timeMillis)

            try {
                context.startForegroundService(startIntent)
            } catch (_: Exception) {
                Toast.makeText(
                    context, R.string.error_cant_start_timer_service, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val timerRepository: TimerRepository get() = Hub.get()

    private val notificationController by lazyTryOrNull {
        QuickTimerNotificationController(applicationContext)
    }

    private val completionScheduler by lazyTryOrNull {
        TimerCompletionScheduler(
            context = applicationContext,
            onReached = timerRepository::notifyTimeReached
        )
    }

    private val buttonClickReceiver = object : BroadcastReceiver() {
        private val pauseResumeDebouncer = Debouncer(1000L)

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PAUSE -> pauseResumeDebouncer.call { timerRepository.pause() }
                ACTION_RESUME -> pauseResumeDebouncer.call { timerRepository.resume() }
                ACTION_CANCEL -> timerRepository.cancel()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        notificationController?.getNotification("")?.let {
            startForeground(NOTIFICATION_ID, it)
        }

        registerButtonClickReceiver()
        observeTimerState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (timerRepository.state.value !is TimerState.Running) {
            timerRepository.start(intent?.getLongExtra(KEY_TIME_MILLIS, 0L) ?: 0L)
        } else {
            Toast.makeText(
                applicationContext, R.string.error_timer_is_already_running, Toast.LENGTH_LONG
            ).show()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        unregisterButtonClickReceiver()
        completionScheduler?.cancel()

        super.onDestroy()
    }

    private fun observeTimerState() {
        var hasStarted = false
        var wasRunning = false

        lifecycleScope.launch {
            timerRepository.state.collect { state ->
                notificationController?.render(state)

                when (state) {
                    is TimerState.Running -> {
                        // (Re)arm the alarm on each entry into Running (start/resume).
                        if (!wasRunning) completionScheduler?.schedule(state.remainingMillis)
                        hasStarted = true
                    }

                    is TimerState.Paused -> completionScheduler?.cancel()

                    TimerState.Finished -> if (hasStarted) {
                        completionScheduler?.cancel()
                        playSound(applicationContext, R.raw.double_ping, onComplete = ::stopSelf)
                    }

                    TimerState.Idle -> if (hasStarted) {
                        completionScheduler?.cancel()
                        stopSelf()
                    }
                }

                wasRunning = state is TimerState.Running
            }
        }
    }

    private fun registerButtonClickReceiver() {
        ContextCompat.registerReceiver(
            applicationContext,
            buttonClickReceiver,
            IntentFilter().apply {
                addAction(ACTION_PAUSE)
                addAction(ACTION_RESUME)
                addAction(ACTION_CANCEL)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun unregisterButtonClickReceiver() {
        try {
            applicationContext.unregisterReceiver(buttonClickReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}