package com.yogitechnolabs.loginmanager.ui

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.yogitechnolabs.loginmanager.R

class CustomDialog(context: Context) {

    private val dialog = Dialog(context)
    private val view: View = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)

    private val titleView: TextView = view.findViewById(R.id.dialogTitle)
    private val messageView: TextView = view.findViewById(R.id.dialogMessage)
    private val iconView: ImageView = view.findViewById(R.id.dialogIcon)
    private val btnPositive: ButtonView = view.findViewById(R.id.btnPositive)
    private val btnNegative: ButtonView = view.findViewById(R.id.btnNegative)


    init {
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(false)
    }

    fun setTitle(title: String): CustomDialog {
        titleView.text = title
        return this
    }

    fun setMessage(message: String): CustomDialog {
        messageView.text = message
        return this
    }

    fun setIcon(iconResId: Int): CustomDialog {
        iconView.setImageResource(iconResId)
        iconView.visibility = View.VISIBLE
        return this
    }

    fun setPositiveButton(text: String, onClick: () -> Unit): CustomDialog {
        btnPositive.setOnClickListener {
            onClick()
            dialog.dismiss()
        }
        btnPositive.findViewById<TextView>(R.id.buttonText).text = text
        return this
    }

    fun setNegativeButton(text: String, onClick: () -> Unit): CustomDialog {
        btnNegative.setOnClickListener {
            onClick()
            dialog.dismiss()
        }
        btnNegative.findViewById<TextView>(R.id.buttonText).text = text
        return this
    }

    fun hideNegativeButton(): CustomDialog {
        btnNegative.visibility = View.GONE
        return this
    }

    fun show(): CustomDialog {
        dialog.show()
        return this
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
