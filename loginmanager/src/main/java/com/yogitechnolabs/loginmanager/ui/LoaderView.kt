package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.yogitechnolabs.loginmanager.R

class LoaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val layoutRoot: LinearLayout
    private val lottie: LottieAnimationView
    private val progressBar: ProgressBar
    private val loadingText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loader, this, true)
        layoutRoot = findViewById(R.id.layoutRoot)
        lottie = findViewById(R.id.lottie)
        progressBar = findViewById(R.id.progressBar)
        loadingText = findViewById(R.id.tvLoadingText)
        hide()
    }

    /**
     * Universal Loader — automatically detects:
     *  - Int (R.raw.loader) → use Lottie from raw
     *  - String (URL) → use Lottie from URL
     *  - null → use default circular ProgressBar
     */
    fun showLoader(source: Any? = null, message: String = "Loading...") {
        setMessage(message)
        layoutRoot.orientation = VERTICAL

        when (source) {
            is Int -> { // From raw resource
                progressBar.visibility = GONE
                lottie.visibility = VISIBLE
                lottie.setAnimation(source)
                lottie.playAnimation()
            }
            is String -> { // From URL
                progressBar.visibility = GONE
                lottie.visibility = VISIBLE
                lottie.setAnimationFromUrl(source)
                lottie.playAnimation()
            }
            else -> { // Default circular loader
                lottie.visibility = GONE
                progressBar.visibility = VISIBLE
            }
        }

        visibility = VISIBLE
    }

    fun setMessage(message: String) {
        loadingText.text = message
    }

    fun hide() {
        lottie.cancelAnimation()
        lottie.visibility = GONE
        progressBar.visibility = GONE
        visibility = GONE
    }
}
