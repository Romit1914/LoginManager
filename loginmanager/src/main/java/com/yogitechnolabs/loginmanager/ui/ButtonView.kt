package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.yogitechnolabs.loginmanager.R

class ButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val buttonContainer: FrameLayout
    private val buttonText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_button, this, true)
        buttonContainer = findViewById(R.id.buttonContainer)
        buttonText = findViewById(R.id.buttonText)

        context.theme.obtainStyledAttributes(attrs, R.styleable.ButtonView, 0, 0).apply {
            try {
                // Text
                getString(R.styleable.ButtonView_btnText)?.let { buttonText.text = it }

                // Text size
                val textSize = getDimension(R.styleable.ButtonView_btnTextSize, 16f)
                buttonText.textSize = textSize / resources.displayMetrics.scaledDensity

                // Text color
                getColor(R.styleable.ButtonView_btnTextColor, Color.WHITE)
                    .let { buttonText.setTextColor(it) }

                // Background color + corner radius
                val bgColor = getColor(R.styleable.ButtonView_btnBackgroundColor, Color.BLUE)
                val corner = getDimension(R.styleable.ButtonView_btnCornerRadius, 8f)
                val bgDrawable = GradientDrawable().apply {
                    setColor(bgColor)
                    cornerRadius = corner
                }

                // Background image (optional)
                val bgImageResId = getResourceId(R.styleable.ButtonView_btnBackgroundImage, 0)
                val finalDrawable: Drawable = if (bgImageResId != 0) {
                    val imageDrawable = ContextCompat.getDrawable(context, bgImageResId)
                    if (imageDrawable != null) {
                        // Combine color layer + image layer
                        LayerDrawable(arrayOf(bgDrawable, imageDrawable))
                    } else {
                        bgDrawable
                    }
                } else {
                    bgDrawable
                }
                buttonContainer.background = finalDrawable

                // Text style
                when (getInt(R.styleable.ButtonView_btnTextStyle, 0)) {
                    0 -> buttonText.setTypeface(buttonText.typeface, Typeface.NORMAL)
                    1 -> buttonText.setTypeface(buttonText.typeface, Typeface.BOLD)
                    2 -> buttonText.setTypeface(buttonText.typeface, Typeface.ITALIC)
                }

                // Custom font
                val fontResId = getResourceId(R.styleable.ButtonView_btnFont, 0)
                if (fontResId != 0) {
                    try {
                        val tf = ResourcesCompat.getFont(context, fontResId)
                        buttonText.typeface = tf
                    } catch (_: Exception) {
                    }
                }

            } finally {
                recycle()
            }
        }

        isClickable = true
        isFocusable = true
    }

    // Programmatic setters
    fun setText(text: String) { buttonText.text = text }
    fun setTextSizePx(size: Float) { buttonText.textSize = size / resources.displayMetrics.scaledDensity }
    fun setTextColor(color: Int) { buttonText.setTextColor(color) }

    override fun setBackgroundColor(color: Int) {
        (buttonContainer.background as? GradientDrawable)?.setColor(color)
    }

    fun setCornerRadius(radius: Float) {
        (buttonContainer.background as? GradientDrawable)?.cornerRadius = radius
    }

    fun setTextStyle(style: Int) {
        when (style) {
            0 -> buttonText.setTypeface(buttonText.typeface, Typeface.NORMAL)
            1 -> buttonText.setTypeface(buttonText.typeface, Typeface.BOLD)
            2 -> buttonText.setTypeface(buttonText.typeface, Typeface.ITALIC)
        }
    }

    fun setFont(@FontRes fontResId: Int) {
        try {
            val tf = ResourcesCompat.getFont(context, fontResId)
            buttonText.typeface = tf
        } catch (_: Exception) {
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        buttonContainer.setOnClickListener(l)
    }
}
