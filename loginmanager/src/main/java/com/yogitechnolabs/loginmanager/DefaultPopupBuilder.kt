package com.yogitechnolabs.loginmanager

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class DefaultPopupBuilder(
    private val activity: Activity,
    private val title: String,
    private val message: String,
    private val imageRes: Int? = null,
    private val primaryText: String = "OK",
    private val secondaryText: String? = null,
    private val autoDismiss: Long = 0L,
    private val onPrimary: (() -> Unit)? = null,
    private val onSecondary: (() -> Unit)? = null
) {

    fun build(): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val view = LayoutInflater.from(activity).inflate(R.layout.default_popup_layout, null)
        builder.setView(view)
        builder.setCancelable(true)

        val titleView = view.findViewById<TextView>(R.id.popupTitle)
        val messageView = view.findViewById<TextView>(R.id.popupMessage)
        val imageView = view.findViewById<ImageView>(R.id.popupImage)
        val primaryBtn = view.findViewById<Button>(R.id.btnPrimary)
        val secondaryBtn = view.findViewById<Button>(R.id.btnSecondary)

        titleView.text = title
        messageView.text = message
        imageRes?.let { imageView.setImageResource(it) } ?: run { imageView.visibility = View.GONE }

        primaryBtn.text = primaryText
        primaryBtn.setOnClickListener {
            onPrimary?.invoke()
            builder.create().dismiss()
        }

        if (secondaryText != null) {
            secondaryBtn.text = secondaryText
            secondaryBtn.visibility = View.VISIBLE
            secondaryBtn.setOnClickListener {
                onSecondary?.invoke()
                builder.create().dismiss()
            }
        } else {
            secondaryBtn.visibility = View.GONE
        }

        val dialog = builder.create()
        if (autoDismiss > 0L) {
            view.postDelayed({ dialog.dismiss() }, autoDismiss)
        }

        dialog.show()
        return dialog
    }
}
