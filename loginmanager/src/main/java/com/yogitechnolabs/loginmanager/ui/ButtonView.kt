package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
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
    private val buttonIcon: ImageView

    init {
        inflate(context, R.layout.view_button, this)
        buttonContainer = findViewById(R.id.buttonContainer)
        buttonText = findViewById(R.id.buttonText)
        buttonIcon = findViewById(R.id.buttonIcon)

        context.theme.obtainStyledAttributes(attrs, R.styleable.ButtonView, 0, 0).apply {
            try {
                // Text
                getString(R.styleable.ButtonView_btnText)?.let { buttonText.text = it }

                // Text size
                val textSize = getDimension(R.styleable.ButtonView_btnTextSize, 16f)
                buttonText.textSize = textSize / resources.displayMetrics.scaledDensity

                // Text color
                buttonText.setTextColor(getColor(R.styleable.ButtonView_btnTextColor, Color.WHITE))

                // Text style
                when (getInt(R.styleable.ButtonView_btnTextStyle, 0)) {
                    1 -> buttonText.setTypeface(buttonText.typeface, Typeface.BOLD)
                    2 -> buttonText.setTypeface(buttonText.typeface, Typeface.ITALIC)
                    else -> buttonText.setTypeface(buttonText.typeface, Typeface.NORMAL)
                }

                // Custom font
                val fontResId = getResourceId(R.styleable.ButtonView_btnFont, 0)
                if (fontResId != 0) {
                    try {
                        val tf = ResourcesCompat.getFont(context, fontResId)
                        buttonText.typeface = tf
                    } catch (_: Exception) {}
                }

                // Icon
                val iconRes = getResourceId(R.styleable.ButtonView_btnIcon, 0)
                if (iconRes != 0) {
                    buttonIcon.setImageResource(iconRes)
                    buttonIcon.imageTintList = null
                    buttonIcon.visibility = VISIBLE
                } else {
                    buttonIcon.visibility = GONE
                }

                // Background setup
                val corner = getDimension(R.styleable.ButtonView_btnCornerRadius, 8f)
                val borderColor = getColor(R.styleable.ButtonView_btnBorderColor, Color.BLUE)
                val borderWidth = getDimension(R.styleable.ButtonView_btnBorderWidth, 0f)
                val bgImageResId = getResourceId(R.styleable.ButtonView_btnBackgroundImage, 0)

                val bgDrawable = GradientDrawable().apply {
                    setColor(getColor(R.styleable.ButtonView_btnBackgroundColor, Color.BLUE))
                    this.cornerRadius = corner
                    if (borderWidth > 0) setStroke(borderWidth.toInt(), borderColor)
                }

                val finalDrawable: Drawable = if (bgImageResId != 0) {
                    val imageDrawable = ContextCompat.getDrawable(context, bgImageResId)
                    if (imageDrawable != null) {
                        // Image ke upar corner radius and border
                        val gd = GradientDrawable().apply {
                            cornerRadius = corner
                            if (borderWidth > 0) setStroke(borderWidth.toInt(), borderColor)
                        }
                        LayerDrawable(arrayOf(gd, imageDrawable))
                    } else bgDrawable
                } else bgDrawable

                buttonContainer.background = finalDrawable

            } finally {
                recycle()
            }
        }

        isClickable = true
        isFocusable = true
        buttonContainer.isClickable = true
        buttonContainer.isFocusable = true
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        buttonContainer.setOnClickListener(l)
    }

    // Programmatic setters
    fun setText(text: String) { buttonText.text = text }
    fun setTextColor(color: Int) { buttonText.setTextColor(color) }
    fun setTextSizePx(size: Float) { buttonText.textSize = size / resources.displayMetrics.scaledDensity }
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
        } catch (_: Exception) {}
    }
    fun setIcon(resId: Int) {
        buttonIcon.setImageResource(resId)
        buttonIcon.visibility = VISIBLE
    }
    fun setCornerRadius(radius: Float) {
        (buttonContainer.background as? GradientDrawable)?.cornerRadius = radius
    }
}
