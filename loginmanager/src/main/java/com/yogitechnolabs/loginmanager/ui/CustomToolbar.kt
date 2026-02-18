package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.databinding.ViewCustomToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomToolbarBinding

    init {
        binding = ViewCustomToolbarBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
x
        // Toolbar root should NOT be clickable
        isClickable = false
        isFocusable = false

        attrs?.let {
            context.withStyledAttributes(it, R.styleable.CustomToolbar) {

                // Title
                binding.tvTitle.text =
                    getString(R.styleable.CustomToolbar_ct_title) ?: ""

                // Back visibility
                binding.ivBack.visibility =
                    if (getBoolean(R.styleable.CustomToolbar_ct_showBack, true))
                        View.VISIBLE else View.GONE

                // Background color
                setBackgroundColor(
                    getColor(
                        R.styleable.CustomToolbar_ct_backgroundColor,
                        Color.TRANSPARENT
                    )
                )

                // Title color
                binding.tvTitle.setTextColor(
                    getColor(
                        R.styleable.CustomToolbar_ct_titleColor,
                        Color.WHITE
                    )
                )

                // Back icon tint
                binding.ivBack.setColorFilter(
                    getColor(
                        R.styleable.CustomToolbar_ct_backIconTint,
                        Color.WHITE
                    )
                )
            }
        }
    }

    /* ---------------- PUBLIC METHODS ---------------- */

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setTitleColor(color: Int) {
        binding.tvTitle.setTextColor(color)
    }

    fun setToolbarBackground(color: Int) {
        setBackgroundColor(color)
    }

    fun setBackIconTint(color: Int) {
        binding.ivBack.setColorFilter(color)
    }

    fun showBack(show: Boolean) {
        binding.ivBack.visibility = if (show) View.VISIBLE else View.GONE
    }

    /** ONLY BACK ICON CLICK */
    fun setOnBackClick(action: () -> Unit) {
        binding.ivBack.setOnClickListener { action() }
    }
}
