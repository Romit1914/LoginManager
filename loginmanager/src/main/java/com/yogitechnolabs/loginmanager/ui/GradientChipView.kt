package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.google.android.material.chip.Chip
import com.yogitechnolabs.loginmanager.R
import androidx.core.content.withStyledAttributes

class GradientChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.chipStyle
) : Chip(context, attrs, defStyleAttr) {

    private var startColor = 0
    private var endColor = 0
    private var selectedStartColor = 0
    private var selectedEndColor = 0
    private var useGradient = false
    private var borderColor = 0
    private var borderWidth = 0f
    private var cornerRadius = 32f
    private var isGradientSelected = false

    init {
        context.withStyledAttributes(attrs, R.styleable.GradientChipView) {

            startColor = getColor(R.styleable.GradientChipView_chipStartColor, 0)
            endColor = getColor(R.styleable.GradientChipView_chipEndColor, startColor)
            selectedStartColor =
                getColor(R.styleable.GradientChipView_chipSelectedStartColor, startColor)
            selectedEndColor = getColor(R.styleable.GradientChipView_chipSelectedEndColor, endColor)
            useGradient = getBoolean(R.styleable.GradientChipView_chipUseGradient, false)
            borderColor = getColor(R.styleable.GradientChipView_chipBorderColor, 0)
            borderWidth = getDimension(R.styleable.GradientChipView_chipBorderWidth, 0f)
            cornerRadius = getDimension(R.styleable.GradientChipView_chipCornerRadius, 32f)

        }

        isCheckable = true
        refreshChipBackground(isChecked)

        setOnClickListener {
            isChecked = !isChecked
            refreshChipBackground(isChecked)
        }
    }

    private fun refreshChipBackground(isSelected: Boolean) {
        val backgroundDrawable = GradientDrawable()

        if (useGradient) {
            backgroundDrawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            if (isSelected) {
                backgroundDrawable.colors = intArrayOf(selectedStartColor, selectedEndColor)
            } else {
                backgroundDrawable.colors = intArrayOf(startColor, endColor)
            }
        } else {
            backgroundDrawable.setColor(if (isSelected) selectedStartColor else startColor)
        }

        backgroundDrawable.cornerRadius = cornerRadius
        if (borderWidth > 0 && borderColor != 0) {
            backgroundDrawable.setStroke(borderWidth.toInt(), borderColor)
        }

        background = backgroundDrawable
    }
}
