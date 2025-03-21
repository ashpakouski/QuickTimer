package com.shpak.quicktimer.presentation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import com.shpak.quicktimer.util.ScreenMetricsCompat

abstract class CustomDialog(context: Context) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun setContentView(view: View) {
        super.setContentView(view)

        window?.setLayout(
            (ScreenMetricsCompat.getScreenSize(context).width * 0.9f).toInt(),
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }
}