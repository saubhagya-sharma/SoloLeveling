package com.example.sololeveling

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.lang.ref.WeakReference

object SystemMessageManager {

    private const val DISPLAY_DURATION_MS = 3000L
    private const val FADE_DURATION_MS = 250L

    private val mainHandler = Handler(Looper.getMainLooper())
    private val messageQueue = ArrayDeque<String>()
    private var isShowing = false
    private var currentOverlay: View? = null
    private var currentRemovalTask: Runnable? = null
    private var hostActivityRef: WeakReference<Activity>? = null

    fun show(activity: Activity, message: String) {
        mainHandler.post {
            messageQueue.addLast(message)
            if (!isShowing) {
                showNext(activity)
            }
        }
    }

    private fun showNext(activity: Activity) {
        if (isShowing) return

        val nextMessage = messageQueue.removeFirstOrNull() ?: run {
            isShowing = false
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            isShowing = false
            showNext(activity)
            return
        }

        val root = activity.window.decorView as? ViewGroup ?: run {
            isShowing = false
            return
        }

        val overlay = LayoutInflater.from(activity).inflate(R.layout.view_system_message, root, false)
        overlay.alpha = 0f

        overlay.findViewById<TextView>(R.id.system_message).text = nextMessage

        val removeTask = Runnable {
            removeCurrentAndShowNext()
        }

        overlay.setOnClickListener {
            currentRemovalTask?.let(mainHandler::removeCallbacks)
            removeCurrentAndShowNext()
        }

        root.addView(overlay)
        overlay.animate().alpha(1f).setDuration(FADE_DURATION_MS).start()

        hostActivityRef = WeakReference(activity)
        currentOverlay = overlay
        currentRemovalTask = removeTask
        isShowing = true

        mainHandler.postDelayed(removeTask, DISPLAY_DURATION_MS)
    }

    private fun removeCurrentAndShowNext() {
        val overlay = currentOverlay
        val hostActivity = hostActivityRef?.get()
        val root = hostActivity?.window?.decorView as? ViewGroup

        if (overlay != null && overlay.parent === root) {
            root?.removeView(overlay)
        }

        currentOverlay = null
        currentRemovalTask = null
        isShowing = false

        if (hostActivity != null) {
            showNext(hostActivity)
        }
    }
}
