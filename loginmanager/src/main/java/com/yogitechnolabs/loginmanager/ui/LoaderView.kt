package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.yogitechnolabs.loginmanager.R

class LoaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val progressBar: ProgressBar
    private val loadingText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loader, this, true)
        progressBar = findViewById(R.id.progressBar)
        loadingText = findViewById(R.id.tvLoadingText)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoaderView,
            0, 0
        ).apply {
            try {
                val text = getString(R.styleable.LoaderView_loaderText)
                if (!text.isNullOrEmpty()) {
                    loadingText.text = text
                }
            } finally {
                recycle()
            }
        }
    }

    fun setMessage(message: String) {
        loadingText.text = message
    }

    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = GONE
    }
}
