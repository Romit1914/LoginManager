package com.yogitechnolabs.loginmanager.ui

import android.R
import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class DropDownComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    // Create AutoCompleteTextView
    private val autoCompleteTextView: AutoCompleteTextView = AutoCompleteTextView(context)

    init {
        autoCompleteTextView.setPadding(16, 16, 16, 16)
        autoCompleteTextView.isSingleLine = true
        this.addView(autoCompleteTextView)

        // Default hint
        hint = "Select an option"
    }

    /**
     * Function to set items in the dropdown
     */
    fun setItems(list: List<String>) {
        val adapter = ArrayAdapter(context, R.layout.simple_list_item_1, list)
        autoCompleteTextView.setAdapter(adapter)
    }

    /**
     * Function to get selected item
     */
    fun getSelectedItem(): String? {
        return autoCompleteTextView.text.toString()
    }

    /**
     * Optional: To set hint dynamically
     */
    fun setHintText(text: String) {
        hint = text
    }
}
