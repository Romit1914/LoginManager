package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yogitechnolabs.loginmanager.R

class ButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val container: FrameLayout
    private val textView: TextView

    private var defaultCornerRadius = 12f
    private var defaultBgColor = ContextCompat.getColor(context, R.color.btn_primary_bg)
    private var defaultTextColor = ContextCompat.getColor(context, android.R.color.white)
    private var defaultTextSize = 16f

    private val bgDrawable = GradientDrawable()

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_button, this, true)
        container = view.findViewById(R.id.buttonContainer)
        textView = view.findViewById(R.id.buttonText)

        // Set default background
        bgDrawable.cornerRadius = defaultCornerRadius
        bgDrawable.setColor(defaultBgColor)
        container.background = bgDrawable

        // Read custom attrs
        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.ButtonView)
            val text = ta.getString(R.styleable.ButtonView_btnText) ?: "Button"
            val textSize = ta.getDimension(R.styleable.ButtonView_btnTextSize, defaultTextSize)
            val bgColor = ta.getColor(R.styleable.ButtonView_btnBackgroundColor, defaultBgColor)
            val textColor = ta.getColor(R.styleable.ButtonView_btnTextColor, defaultTextColor)
            val cornerRadius = ta.getDimension(R.styleable.ButtonView_btnCornerRadius, defaultCornerRadius)
            ta.recycle()

            setText(text)
            setTextSizePx(textSize)
            setTextColor(textColor)
            setBackgroundColor(bgColor)
            setCornerRadius(cornerRadius)
        }

        container.isClickable = true
        container.isFocusable = true
    }

    override fun setOnClickListener(l: OnClickListener?) {
        container.setOnClickListener(l)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val newWidthMeasureSpec = if (widthMode == MeasureSpec.UNSPECIFIED) {
            // wrap_content → measure normally
            widthMeasureSpec
        } else {
            // match_parent ya fixed → make container full width
            MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        }

        super.onMeasure(newWidthMeasureSpec, heightMeasureSpec)
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setTextSizePx(size: Float) {
        textView.textSize = size / resources.displayMetrics.scaledDensity
    }

    fun setTextColor(color: Int) {
        textView.setTextColor(color)
    }

    override fun setBackgroundColor(color: Int) {
        bgDrawable.setColor(color)
        container.background = bgDrawable
    }

    fun setCornerRadius(radius: Float) {
        bgDrawable.cornerRadius = radius
        container.background = bgDrawable
    }
}
