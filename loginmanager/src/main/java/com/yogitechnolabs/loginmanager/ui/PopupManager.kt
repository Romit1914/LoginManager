package com.yogitechnolabs.loginmanager.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.yogitechnolabs.loginmanager.R


enum class PopupType {
    NORMAL,
    FULLSCREEN
}

object PopupManager {

    private var lastShownTime: Long = 0L
    private const val POPUP_INTERVAL = 30_000L // 30 sec gap

    fun show(
        context: Context,
        type: PopupType = PopupType.NORMAL,
        title: String = "",
        message: String = "",
        @LayoutRes customLayout: Int? = null,
        @DrawableRes backgroundRes: Int? = null,
        autoDismiss: Boolean = false,
        onOkClick: (() -> Unit)? = null
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShownTime < POPUP_INTERVAL) return
        lastShownTime = currentTime

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val layoutRes = customLayout ?: (
                if (type == PopupType.FULLSCREEN)
                    R.layout.layout_popup_fullscreen
                else
                    R.layout.layout_popup_dialog
                )

        val view = LayoutInflater.from(context).inflate(layoutRes, null)
        dialog.setContentView(view)

        // Default setup if default layout used
        view.findViewById<TextView?>(R.id.tvTitle)?.text = title
        view.findViewById<TextView?>(R.id.tvMessage)?.text = message

        view.findViewById<Button?>(R.id.btnOk)?.setOnClickListener {
            onOkClick?.invoke()
            dialog.dismiss()
        }

        // Close button for fullscreen
        view.findViewById<ImageButton?>(R.id.btnClose)?.setOnClickListener {
            dialog.dismiss()
        }

        // Optional background image
        if (type == PopupType.FULLSCREEN && backgroundRes != null) {
            view.findViewById<ImageView?>(R.id.bgImage)?.setImageResource(backgroundRes)
        }

        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (type == PopupType.FULLSCREEN) {
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }

        dialog.show()

        if (autoDismiss) {
            view.postDelayed({ dialog.dismiss() }, 4000)
        }
    }
}
