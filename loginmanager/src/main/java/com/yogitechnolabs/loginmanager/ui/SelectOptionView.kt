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

    init {
        LayoutInflater.from(context).inflate(R.layout.view_select_option, this, true)
        radioButton = findViewById(R.id.radioButton)
        checkBox = findViewById(R.id.checkBox)
        textView = findViewById(R.id.tvOptionText)

        context.theme.obtainStyledAttributes(attrs, R.styleable.SelectOptionView, 0, 0).apply {
            try {
                val optionType = getString(R.styleable.SelectOptionView_optionType) ?: "checkbox"
                val optionText = getString(R.styleable.SelectOptionView_optionText) ?: ""
                textView.text = optionText

                if (optionType == "radio") {
                    isRadio = true
                    radioButton.visibility = VISIBLE
                } else {
                    isRadio = false
                    checkBox.visibility = VISIBLE
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
