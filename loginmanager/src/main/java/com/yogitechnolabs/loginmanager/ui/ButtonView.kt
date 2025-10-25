package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.graphics.Color
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
 *  - Primary, Secondary, Outline, Gradient, Custom
 *  - Icon + Text combinations
 *  - Loading state
 *  - Fully customizable corner radius, colors, and text size
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
    private var styleType: Int = 0 // 0=Primary,1=Secondary,2=Outline,3=Gradient,4=Custom

    // Customizable properties
    private var cornerRadius = 0f
    private var backgroundColor = 0
    private var textColor = 0
    private var textSize = 16f

    init {
        LayoutInflater.from(context).inflate(R.layout.view_button, this, true)
        tvText = findViewById(R.id.tvButtonText)
        ivIcon = findViewById(R.id.ivButtonIcon)
        progressBar = findViewById(R.id.progressButton)

        isClickable = true
        isFocusable = true

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ButtonView)
        try {
            // Base attributes
            tvText.text = ta.getString(R.styleable.ButtonView_btnText) ?: "Button"
            styleType = ta.getInt(R.styleable.ButtonView_btnStyle, 0)
            cornerRadius = ta.getDimension(R.styleable.ButtonView_btnCornerRadius, resources.getDimension(R.dimen.button_corner_radius))
            backgroundColor = ta.getColor(R.styleable.ButtonView_btnBackgroundColor, ContextCompat.getColor(context, R.color.btn_primary_bg))
            textColor = ta.getColor(R.styleable.ButtonView_btnTextColor, ContextCompat.getColor(context, R.color.white))
            textSize = ta.getDimension(R.styleable.ButtonView_btnTextSize, 16f)

            val iconRes = ta.getResourceId(R.styleable.ButtonView_btnIcon, 0)
            if (iconRes != 0) {
                ivIcon.setImageResource(iconRes)
                ivIcon.visibility = VISIBLE
            } else {
                ivIcon.visibility = GONE
            }

            applyStyle()
        } finally {
            ta.recycle()
        }
    }

    private fun applyStyle() {
        val bg = GradientDrawable()
        bg.cornerRadius = cornerRadius

        when (styleType) {
            0 -> { // Primary
                bg.setColor(ContextCompat.getColor(context, R.color.btn_primary_bg))
                tvText.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            1 -> { // Secondary
                bg.setColor(ContextCompat.getColor(context, R.color.btn_secondary_bg))
                tvText.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            2 -> { // Outline
                bg.setColor(Color.TRANSPARENT)
                bg.setStroke(3, ContextCompat.getColor(context, R.color.btn_outline_border))
                tvText.setTextColor(ContextCompat.getColor(context, R.color.btn_outline_text))
            }
            3 -> { // Gradient
                bg.colors = intArrayOf(
                    ContextCompat.getColor(context, R.color.btn_gradient_start),
                    ContextCompat.getColor(context, R.color.btn_gradient_end)
                )
                bg.orientation = GradientDrawable.Orientation.LEFT_RIGHT
                tvText.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            else -> { // Custom (Developer-defined)
                bg.setColor(backgroundColor)
                tvText.setTextColor(textColor)
            }
        }

        background = bg
        tvText.textSize = textSize
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

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        this.isClickable = true
    }
}
