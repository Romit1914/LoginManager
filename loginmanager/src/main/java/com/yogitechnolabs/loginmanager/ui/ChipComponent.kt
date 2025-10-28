package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.yogitechnolabs.loginmanager.R

class ChipComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val chipGroup: ChipGroup
    private var options: List<String> = listOf()
    private var isSingleSelection: Boolean = true
    private var onSelectionChange: ((List<String>) -> Unit)? = null

    // Default styling
    private var chipSolidColor: Int = ContextCompat.getColor(context, R.color.btn_outline_border)
    private var chipTextColor: Int = ContextCompat.getColor(context, android.R.color.white)
    private var chipGradientStartColor: Int = 0
    private var chipGradientEndColor: Int = 0
    private var useGradient: Boolean = false

    init {
        LayoutInflater.from(context).inflate(R.layout.view_chip_component, this, true)
        chipGroup = findViewById(R.id.chipGroup)

        // Custom attributes
        context.theme.obtainStyledAttributes(attrs, R.styleable.ChipComponent, 0, 0).apply {
            try {
                chipSolidColor = getColor(
                    R.styleable.ChipComponent_chipSolidColor,
                    ContextCompat.getColor(context, R.color.btn_outline_border)
                )
                chipTextColor = getColor(
                    R.styleable.ChipComponent_chipTextColor,
                    ContextCompat.getColor(context, android.R.color.white)
                )
                chipGradientStartColor = getColor(
                    R.styleable.ChipComponent_chipGradientStartColor,
                    0
                )
                chipGradientEndColor = getColor(
                    R.styleable.ChipComponent_chipGradientEndColor,
                    0
                )
                useGradient = getBoolean(R.styleable.ChipComponent_useGradient, false)
            } finally {
                recycle()
            }
        }
        chipGroup.isSingleSelection = isSingleSelection
    }

    /** Enable or disable single selection */
    fun setSingleSelection(single: Boolean) {
        isSingleSelection = single
        chipGroup.isSingleSelection = single
    }

    /** Set chip options dynamically */
    fun setOptions(optionList: List<String>) {
        options = optionList
        chipGroup.removeAllViews()

        optionList.forEachIndexed { index, option ->
            val chip = Chip(context).apply {
                text = option
                isCheckable = true
                isClickable = true
                id = generateViewId()
                setTextColor(chipTextColor)
                background = if (useGradient && chipGradientStartColor != 0 && chipGradientEndColor != 0) {
                    GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        intArrayOf(chipGradientStartColor, chipGradientEndColor)
                    ).apply {
                        cornerRadius = 50f
                    }
                } else {
                    GradientDrawable().apply {
                        cornerRadius = 50f
                        setColor(chipSolidColor)
                    }
                }
            }
            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedTexts = checkedIds.mapNotNull {
                val chip = group.findViewById<Chip>(it)
                chip?.text?.toString()
            }
            onSelectionChange?.invoke(selectedTexts)
        }
    }

    /** Get selected chips */
    fun getSelectedOptions(): List<String> {
        return chipGroup.checkedChipIds.mapNotNull {
            val chip = chipGroup.findViewById<Chip>(it)
            chip?.text?.toString()
        }
    }

    /** Listen for selection changes */
    fun setOnSelectionChangeListener(listener: (List<String>) -> Unit) {
        this.onSelectionChange = listener
    }

    /** Allow programmatic color customization */
    fun setChipColors(
        solidColor: Int? = null,
        textColor: Int? = null,
        gradientStart: Int? = null,
        gradientEnd: Int? = null
    ) {
        solidColor?.let { chipSolidColor = it }
        textColor?.let { chipTextColor = it }
        if (gradientStart != null && gradientEnd != null) {
            useGradient = true
            chipGradientStartColor = gradientStart
            chipGradientEndColor = gradientEnd
        }
    }
}
