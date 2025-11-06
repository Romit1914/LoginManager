package com.yogitechnolabs.loginmanager.ui

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.yogitechnolabs.loginmanager.DefaultPopupBuilder

object PopupManager {

    private var currentDialog: AlertDialog? = null

    /**
     * Show default popup
     */
    fun showDefault(
        activity: Activity,
        title: String = "Title",
        message: String = "Message",
        imageRes: Int? = null,
        primaryText: String = "OK",
        secondaryText: String? = null,
        autoDismiss: Long = 0L,
        onPrimary: (() -> Unit)? = null,
        onSecondary: (() -> Unit)? = null
    ) {
        dismiss()
        currentDialog = DefaultPopupBuilder(
            activity, title, message, imageRes,
            primaryText, secondaryText, autoDismiss, onPrimary, onSecondary
        ).build()
    }

    /**
     * Show custom popup using developer-provided layout
     */
    fun showCustom(
        activity: Activity,
        @LayoutRes layoutRes: Int,
        autoDismiss: Long = 0L,
        onViewAction: ((View) -> Unit)? = null
    ) {
        dismiss()

        val builder = AlertDialog.Builder(activity)
        val view = LayoutInflater.from(activity).inflate(layoutRes, null)
        builder.setView(view)
        builder.setCancelable(true)

        currentDialog = builder.create()
        currentDialog?.show()

        // Auto dismiss
        if (autoDismiss > 0L) {
            view.postDelayed({ dismiss() }, autoDismiss)
        }

        // Click callback
        view.setOnClickListener { v ->
            onViewAction?.invoke(v)
        }
    }

    /**
     * Dismiss current popup
     */
    fun dismiss() {
        currentDialog?.dismiss()
        currentDialog = null
    }
}
