package com.yogitechnolabs.loginmanager.ui


import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yogitechnolabs.loginmanager.R

/**
 * Reusable, theme-compatible ButtonView
 * Supports:
 *  - primary, secondary, outline, gradient styles
 *  - icon + text combinations
 *  - loading state
 *  - disabled state
 */
class ButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var tvText: TextView
    private var ivIcon: ImageView
    private var progressBar: ProgressBar

    private var isLoading = false
    private var styleType: Int = 0 // 0=Primary,1=Secondary,2=Outline,3=Gradient

    init {
        LayoutInflater.from(context).inflate(R.layout.view_button, this, true)
        tvText = findViewById(R.id.tvButtonText)
        ivIcon = findViewById(R.id.ivButtonIcon)
        progressBar = findViewById(R.id.progressButton)

        isClickable = true
        isFocusable = true

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ButtonView,
            0, 0
        ).apply {
            try {
                tvText.text = getString(R.styleable.ButtonView_btnText) ?: "Button"
                styleType = getInt(R.styleable.ButtonView_btnStyle, 0)
                val iconRes = getResourceId(R.styleable.ButtonView_btnIcon, 0)
                if (iconRes != 0) {
                    ivIcon.setImageResource(iconRes)
                    ivIcon.visibility = VISIBLE
                } else {
                    ivIcon.visibility = GONE
                }
                applyStyle()
            } finally {
                recycle()
            }
        }
    }

    private fun applyStyle() {
        var background = GradientDrawable()
        background.cornerRadius = resources.getDimension(R.dimen.button_corner_radius)

        when (styleType) {
            0 -> { // Primary
                background.setColor(ContextCompat.getColor(context, R.color.btn_primary_bg))
                tvText.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            1 -> { // Secondary
                background.setColor(ContextCompat.getColor(context, R.color.btn_secondary_bg))
                tvText.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            2 -> { // Outline
                background.setColor(ContextCompat.getColor(context, android.R.color.transparent))
                background.setStroke(3, ContextCompat.getColor(context, R.color.btn_outline_border))
                tvText.setTextColor(ContextCompat.getColor(context, R.color.btn_outline_text))
            }
            3 -> { // Gradient
                background.colors = intArrayOf(
                    ContextCompat.getColor(context, R.color.btn_gradient_start),
                    ContextCompat.getColor(context, R.color.btn_gradient_end)
                )
                background.orientation = GradientDrawable.Orientation.LEFT_RIGHT
                tvText.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

        background = background
        this.background = background
    }

    fun setLoading(loading: Boolean) {
        isLoading = loading
        if (loading) {
            tvText.visibility = GONE
            ivIcon.visibility = GONE
            progressBar.visibility = VISIBLE
            isClickable = false
        } else {
            tvText.visibility = VISIBLE
            progressBar.visibility = GONE
            isClickable = true
        }
    }

    fun setText(text: String) {
        tvText.text = text
    }

    fun setButtonStyle(style: Int) {
        styleType = style
        applyStyle()
    }

    fun setIcon(resId: Int) {
        ivIcon.setImageResource(resId)
        ivIcon.visibility = VISIBLE
    }
}
