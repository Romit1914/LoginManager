package com.yogitechnolabs.loginmanager.ui

import android.content.Context
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

    // Default colors
    private var chipBackgroundColor: Int = ContextCompat.getColor(context, R.color.btn_gradient_start)
    private var chipTextColor: Int = ContextCompat.getColor(context, android.R.color.white)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_chip_component, this, true)
        chipGroup = findViewById(R.id.chipGroup)
        chipGroup.isSingleSelection = isSingleSelection

        // Read XML attributes (optional customization)
        context.theme.obtainStyledAttributes(attrs, R.styleable.ChipComponent, 0, 0).apply {
            try {
                chipBackgroundColor = getColor(
                    R.styleable.ChipComponent_chipBackgroundColor,
                    ContextCompat.getColor(context, R.color.btn_gradient_end)
                )
                chipTextColor = getColor(
                    R.styleable.ChipComponent_chipTextColor,
                    ContextCompat.getColor(context, android.R.color.white)
                )
            } finally {
                recycle()
            }
        }
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

        optionList.forEach { option ->
            val chip = Chip(context).apply {
                text = option
                isCheckable = true
                isClickable = true
                id = generateViewId()

                // Text & Background color
                setTextColor(chipTextColor)
                setChipBackgroundColorResource(R.color.btn_gradient_start)

                // Remove stroke, icons, and make rounded
                chipStrokeWidth = 0f
                isChipIconVisible = false
                isCloseIconVisible = false
                shapeAppearanceModel = shapeAppearanceModel.withCornerSize(50f)
                rippleColor = null // optional: remove ripple if not needed
            }
            chipGroup.addView(chip)
        }

        // Handle selection change
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

    /** Set listener for selection change */
    fun setOnSelectionChangeListener(listener: (List<String>) -> Unit) {
        this.onSelectionChange = listener
    }

    /** Programmatically set colors */
    fun setChipColors(backgroundColor: Int, textColor: Int) {
        chipBackgroundColor = backgroundColor
        chipTextColor = textColor
    }
}
