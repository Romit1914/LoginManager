package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
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

    init {
        LayoutInflater.from(context).inflate(R.layout.view_chip_component, this, true)
        chipGroup = findViewById(R.id.chipGroup)
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
                id = generateViewId() // ⚠️ must give unique ID to work properly
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
}
