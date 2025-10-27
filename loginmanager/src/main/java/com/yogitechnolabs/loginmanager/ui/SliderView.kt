package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.slider.Slider
import com.yogitechnolabs.loginmanager.R

class SliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelText: TextView
    private val valueText: TextView
    private val slider: Slider

    init {
        LayoutInflater.from(context).inflate(R.layout.view_slider, this, true)
        orientation = VERTICAL

        labelText = findViewById(R.id.tvSliderLabel)
        valueText = findViewById(R.id.tvSliderValue)
        slider = findViewById(R.id.customSlider)

        context.theme.obtainStyledAttributes(attrs, R.styleable.SliderView, 0, 0).apply {
            try {
                labelText.text = getString(R.styleable.SliderView_sliderLabel) ?: "Label"
                slider.valueFrom = getFloat(R.styleable.SliderView_sliderMin, 0f)
                slider.valueTo = getFloat(R.styleable.SliderView_sliderMax, 100f)
                slider.value = getFloat(R.styleable.SliderView_sliderValue, 50f)

                getColor(R.styleable.SliderView_sliderActiveColor, 0).takeIf { it != 0 }?.let {
                    slider.trackActiveTintList = android.content.res.ColorStateList.valueOf(it)
                }

                getColor(R.styleable.SliderView_sliderInactiveColor, 0).takeIf { it != 0 }?.let {
                    slider.trackInactiveTintList = android.content.res.ColorStateList.valueOf(it)
                }

                getColor(R.styleable.SliderView_sliderThumbColor, 0).takeIf { it != 0 }?.let {
                    slider.thumbTintList = android.content.res.ColorStateList.valueOf(it)
                }

                valueText.text = slider.value.toInt().toString()

                slider.addOnChangeListener { _, value, _ ->
                    valueText.text = value.toInt().toString()
                }

            } finally {
                recycle()
            }
        }
    }

    fun getValue(): Float = slider.value
    fun setValue(value: Float) {
        slider.value = value
        valueText.text = value.toInt().toString()
    }

    fun setOnValueChanged(listener: (Float) -> Unit) {
        slider.addOnChangeListener { _, value, _ ->
            listener(value)
        }
    }
}
