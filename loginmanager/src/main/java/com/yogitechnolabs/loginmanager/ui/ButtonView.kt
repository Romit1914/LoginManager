package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
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
        inflate(context, R.layout.view_button, this)
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
                buttonText.setTextColor(
                    getColor(R.styleable.ButtonView_btnTextColor, Color.WHITE)
                )

                // Background color + corner + border
                val bgColor = getColor(R.styleable.ButtonView_btnBackgroundColor, Color.BLUE)
                val corner = getDimension(R.styleable.ButtonView_btnCornerRadius, 8f)
                val borderColor = getColor(R.styleable.ButtonView_btnBorderColor, Color.BLUE)
                val borderWidth = getDimension(R.styleable.ButtonView_btnBorderWidth, 0f)

                // Optional background image
                val bgImageResId = getResourceId(R.styleable.ButtonView_btnBackgroundImage, 0)
                val finalDrawable: Drawable = if (bgImageResId != 0) {
                    // Image set hai, sirf image dikhe, corner radius apply
                    val imageDrawable = ContextCompat.getDrawable(context, bgImageResId)
                    if (imageDrawable != null) {
                        val gd = GradientDrawable().apply {
                            cornerRadius = corner
                            if (borderWidth > 0) setStroke(borderWidth.toInt(), borderColor)
                        }
                        // Image ke upar drawable wrap karne ke liye LayerDrawable ya ShapeDrawable
                        LayerDrawable(arrayOf(gd, imageDrawable))
                    } else {
                        GradientDrawable().apply { setColor(Color.BLUE); cornerRadius = corner }
                    }
                } else {
                    // Image nahi hai → default blue background
                    val bgColor = getColor(R.styleable.ButtonView_btnBackgroundColor, Color.BLUE)
                    GradientDrawable().apply { setColor(bgColor); cornerRadius = corner;
                        if (borderWidth > 0) setStroke(borderWidth.toInt(), borderColor)
                    }
                }

                buttonContainer.background = finalDrawable

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
}
