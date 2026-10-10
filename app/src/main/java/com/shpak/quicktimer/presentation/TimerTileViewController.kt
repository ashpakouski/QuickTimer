package com.shpak.quicktimer.presentation

import android.app.Dialog
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.shpak.quicktimer.R
import com.shpak.quicktimer.di.Hub
import com.shpak.quicktimer.domain.analytics.AnalyticsEvent
import com.shpak.quicktimer.domain.analytics.AnalyticsLogger
import com.shpak.quicktimer.util.areNotificationsEnabled
import com.shpak.timer.core.TimerStore

class TimerTileViewController : TileService() {

    private var permissionRequestDialog: Dialog? = null
    private var timerSettingsDialog: Dialog? = null

    private val analytics by lazy { Hub.get<AnalyticsLogger>() }
    private val timerStore by lazy { Hub.get<TimerStore>() }

    override fun onTileAdded() {
        super.onTileAdded()

        setInactive()
    }

    override fun onStartListening() {
        super.onStartListening()

        setInactive()
    }

    override fun onClick() {
        super.onClick()

        analytics.log(AnalyticsEvent.TileClick(timerStore.state.value))

        try {
            if (areNotificationsEnabled(applicationContext)) {
                showTimerSettingsDialog()
            } else {
                requestNotificationsPermission()
            }
        } catch (e: Exception) {
            analytics.logException(e)
            Toast.makeText(
                applicationContext, R.string.error_cant_show_dialog, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showTimerSettingsDialog() {
        if (timerSettingsDialog?.isShowing == true) return

        timerSettingsDialog = TimerSetupBottomSheet(applicationContext).apply {
            showDialog(this)
        }
    }

    private fun requestNotificationsPermission() {
        if (permissionRequestDialog?.isShowing == true) return

        permissionRequestDialog = NotificationPermissionBottomSheet(applicationContext).apply {
            showDialog(this)
        }
    }

    private fun setInactive() {
        qsTile?.apply {
            state = Tile.STATE_INACTIVE
            updateTile()
        }
    }
}