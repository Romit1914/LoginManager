package com.yogitechnolabs.loginmanager.ui

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.*
import com.google.android.material.textfield.TextInputLayout

class DropDownComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val editTextView: EditText = EditText(context)
    private var items: List<String> = listOf()
    private var selectedItem: String? = null

    init {
        // Add EditText to TextInputLayout
        editTextView.setPadding(16, 16, 16, 16)
        editTextView.isSingleLine = true
        editTextView.isFocusable = false
        editTextView.isClickable = true
        editTextView.inputType = 0
        this.addView(editTextView)

        hint = "Select an option"

        // Show dropdown dialog on click
        editTextView.setOnClickListener {
            showSearchableDialog()
        }
    }

    /** Function to set items in dropdown */
    fun setItems(list: List<String>) {
        items = list
    }

    /** Function to get selected item */
    fun getSelectedItem(): String? {
        return selectedItem
    }

    /** Optional: Set hint dynamically */
    fun setHintText(text: String) {
        hint = text
    }

    /** Show custom dialog with search + list */
    private fun showSearchableDialog() {
        val dialogView = LinearLayout(context)
        dialogView.orientation = LinearLayout.VERTICAL
        dialogView.setPadding(24, 24, 24, 24)

        val searchEditText = EditText(context)
        searchEditText.hint = "Search..."
        searchEditText.setPadding(16, 16, 16, 16)
        dialogView.addView(searchEditText)

        val listView = ListView(context)
        dialogView.addView(listView)

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Search filter logic
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtered = items.filter { it.contains(s.toString(), ignoreCase = true) }
                listView.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, filtered)
            }
        })

        // Handle item click
        listView.setOnItemClickListener { _, _, position, _ ->
            selectedItem = listView.adapter.getItem(position) as String
            editTextView.setText(selectedItem)
            dialog.dismiss()
        }

        dialog.show()
    }
}
