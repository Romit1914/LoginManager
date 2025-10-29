package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
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

    // 🎨 Default colors (developer can override)
    private var chipBgColor: Int = ContextCompat.getColor(context, R.color.btn_gradient_start)
    private var chipTextColor: Int = ContextCompat.getColor(context, android.R.color.white)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_chip_component, this, true)
        chipGroup = findViewById(R.id.chipGroup)
        chipGroup.isSingleSelection = isSingleSelection

        // Allow color customization from XML
        context.theme.obtainStyledAttributes(attrs, R.styleable.ChipComponent, 0, 0).apply {
            try {
                chipBgColor = getColor(
                    R.styleable.ChipComponent_chipBackgroundColor,
                    chipBgColor
                )
                chipTextColor = getColor(
                    R.styleable.ChipComponent_chipTextColor,
                    chipTextColor
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

    /** Dynamically set chip options */
    fun setOptions(optionList: List<String>) {
        options = optionList
        chipGroup.removeAllViews()

        optionList.forEach { option ->
            val chip = Chip(context).apply {
                text = option
                isCheckable = true
                isClickable = true
                id = generateViewId()

                // ✅ Apply developer-defined colors
                setChipBackgroundColor(ColorStateList.valueOf(chipBgColor))
                setTextColor(chipTextColor)

                chipStrokeWidth = 0f
                shapeAppearanceModel = shapeAppearanceModel.withCornerSize(50f)
                rippleColor = null
            }
            chipGroup.addView(chip)
        }

        // Handle selection changes
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

    /** 🎨 Developer can change chip colors dynamically */
    fun setChipColors(backgroundColor: Any, textColor: Int) {
        chipTextColor = textColor

        when (backgroundColor) {
            is Int -> {
                // Simple solid color
                chipBgColor = backgroundColor
                chipGroup.children.forEach { view ->
                    if (view is Chip) {
                        view.chipBackgroundColor = ColorStateList.valueOf(backgroundColor)
                        view.setTextColor(textColor)
                    }
                }
            }

            is ColorStateList -> {
                // ColorStateList (selector)
                chipGroup.children.forEach { view ->
                    if (view is Chip) {
                        view.chipBackgroundColor = backgroundColor
                        view.setTextColor(textColor)
                    }
                }
            }
        }
    }

}
