package com.hilog.hiloglib.printer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hilog.hiloglib.utils.dp2px
import kotlin.math.log


class HiLogPrinterProvider constructor(val app: Application, var recyclerView: RecyclerView) {
    var floatingView: View? = null
    var isOpen: Boolean = false
    var logView: FrameLayout? = null
    val TAG_FLOATING_VIEW = "TAG_FLOATING_VIEW"
    val TAG_LOG_VIEW = "TAG_FLOATINTAG_LOG_VIEWG_VIEW"
    val windowManager: WindowManager

    init {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.END

        params.x = 0
        params.y = 200.dp2px(app)
        windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    }


    var isAddFloatingView = false
    fun showFloatingView() {
        // Add the floating widget view to the WindowManager
        if (!isAddFloatingView) {
            isAddFloatingView = true
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            params.gravity = Gravity.BOTTOM or Gravity.END
            params.x = 0
            params.y = 160.dp2px(recyclerView.context)
            floatingView = genFloatingView()
            floatingView?.tag = TAG_FLOATING_VIEW
            floatingView?.setBackgroundColor(Color.BLACK)
            floatingView?.alpha = 0.8f
            floatingView?.layoutParams = params
            windowManager.addView(floatingView, params)
            floatingView?.setOnClickListener {
                showLogView()
            }
        }

    }

    fun hideFloatingView() {
        if (isAddFloatingView) {
            windowManager.removeView(floatingView)
            isAddFloatingView = false
        }
    }

    private fun genFloatingView(): View {

        if (floatingView != null) {
            return floatingView!!
        }
        val textView = TextView(app)
        textView.setOnClickListener {
            if (!isAddLogView) {
                showLogView()
            }
        }
        textView.setText("hiLog")
        floatingView = textView
        return floatingView!!
    }

    var isAddLogView = false
    private fun showLogView() {
        if (!isAddLogView) {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                200.dp2px(app),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.BOTTOM
            val logView = genLogView()
            logView.layoutParams = params
            windowManager.addView(logView, params)
            hideFloatingView()
        }
    }

    private fun genLogView(): View {
        if (logView != null) {
            return logView!!
        }
        val logView = FrameLayout(app)
        logView.setBackgroundColor(Color.BLACK)
        logView.addView(recyclerView)
        val logViewParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        logView.layoutParams = logViewParams

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.END


        val closeView = TextView(app)
        closeView.setTextColor(Color.WHITE)
        closeView.layoutParams = params
        closeView.setText("closeView")
        closeView.setOnClickListener {
            closeLogView()
        }
        logView.addView(closeView)
        this.logView = logView
        return logView
    }

    private fun closeLogView() {
        isOpen = false
        isAddLogView = false
        windowManager.removeView(genLogView())
        showFloatingView()
    }

}


