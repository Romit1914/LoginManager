package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.yogitechnolabs.loginmanager.R

class BottomConfirmBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val btnConfirm: TextView
    private val btnCancel: TextView

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.view_bottom_confirm_bar, this, true)

        btnConfirm = findViewById(R.id.btnConfirm)
        btnCancel = findViewById(R.id.btnCancel)

        hide()
    }

    fun setConfirmText(text: String) {
        btnConfirm.text = text
    }

    fun setCancelText(text: String) {
        btnCancel.text = text
    }

    fun setOnConfirm(action: () -> Unit) {
        btnConfirm.setOnClickListener { action.invoke() }
    }

    fun setOnCancel(action: () -> Unit) {
        btnCancel.setOnClickListener { action.invoke() }
    }

    fun show() {
        visibility = View.VISIBLE
        animate().translationY(0f).setDuration(200).start()
    }

    fun hide() {
        visibility = View.GONE
    }
}