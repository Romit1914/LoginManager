package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.yogitechnolabs.loginmanager.R

class SelectOptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val radioButton: RadioButton
    private val checkBox: CheckBox
    private val textView: TextView
    private var isRadio = false
    private var groupName: String? = null

    companion object {
        // Static map to track all radio buttons in same group
        private val radioGroups = mutableMapOf<String, MutableList<SelectOptionView>>()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_select_option, this, true)
        radioButton = findViewById(R.id.radioButton)
        checkBox = findViewById(R.id.checkBox)
        textView = findViewById(R.id.tvOptionText)

        context.theme.obtainStyledAttributes(attrs, R.styleable.SelectOptionView, 0, 0).apply {
            try {
                val optionType = getString(R.styleable.SelectOptionView_optionType) ?: "checkbox"
                val optionText = getString(R.styleable.SelectOptionView_optionText) ?: ""
                groupName = getString(R.styleable.SelectOptionView_optionGroup)
                textView.text = optionText

                if (optionType == "radio") {
                    isRadio = true
                    radioButton.visibility = VISIBLE
                    checkBox.visibility = GONE

                    // Add this radio to its group
                    groupName?.let {
                        val groupList = radioGroups.getOrPut(it) { mutableListOf() }
                        groupList.add(this@SelectOptionView)
                    }

                    // When checked, uncheck others in the same group
                    radioButton.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            groupName?.let { group ->
                                radioGroups[group]?.forEach { other ->
                                    if (other != this@SelectOptionView) {
                                        other.setChecked(false)
                                    }
                                }
                            }
                        }
                    }

                } else {
                    isRadio = false
                    checkBox.visibility = VISIBLE
                    radioButton.visibility = GONE
                }

            } finally {
                recycle()
            }
        }
    }

    fun isChecked(): Boolean =
        if (isRadio) radioButton.isChecked else checkBox.isChecked

    fun setChecked(value: Boolean) {
        if (isRadio) radioButton.isChecked = value else checkBox.isChecked = value
    }

    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        if (isRadio)
            radioButton.setOnCheckedChangeListener { _, isChecked -> listener(isChecked) }
        else
            checkBox.setOnCheckedChangeListener { _, isChecked -> listener(isChecked) }
    }
}
