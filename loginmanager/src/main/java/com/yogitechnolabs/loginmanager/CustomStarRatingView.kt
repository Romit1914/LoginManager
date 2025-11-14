package com.yogitechnolabs.loginmanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toColorInt

class CustomStarRatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var starCount = 5
    private var rating = 0f
    private var starSize = 80
    private var starGap = 16

    private var filledColor = "#FFD700".toColorInt()
    private var emptyColor = "#D3D3D3".toColorInt()
    private var strokeColor = "#AFAFAF".toColorInt()

    private var starDrawableFilled: Int = 0
    private var starDrawableEmpty: Int = 0

    private val stars = ArrayList<ImageView>()
    private var onRatingChange: ((Float) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        loadAttributes(attrs)
        createStars()
        setTouchListener()
    }

    private fun loadAttributes(attrs: AttributeSet?) {
        attrs ?: return
        context.withStyledAttributes(attrs, R.styleable.CustomStarRatingView) {

            starCount = getInt(R.styleable.CustomStarRatingView_starCount, 5)
            starSize = getDimensionPixelSize(R.styleable.CustomStarRatingView_starSize, 80)
            starGap = getDimensionPixelSize(R.styleable.CustomStarRatingView_starGap, 16)

            filledColor = getColor(R.styleable.CustomStarRatingView_filledColor, filledColor)
            emptyColor = getColor(R.styleable.CustomStarRatingView_emptyColor, emptyColor)
            strokeColor = getColor(R.styleable.CustomStarRatingView_strokeColor, strokeColor)

            starDrawableFilled =
                getResourceId(R.styleable.CustomStarRatingView_starFilledDrawable, 0)
            starDrawableEmpty = getResourceId(R.styleable.CustomStarRatingView_starEmptyDrawable, 0)

        }
    }

    private fun createStars() {
        removeAllViews()
        stars.clear()

        for (i in 1..starCount) {
            val star = ImageView(context)
            star.layoutParams = LayoutParams(starSize, starSize).apply {
                marginEnd = starGap
            }

            if (starDrawableEmpty != 0) {
                star.setImageResource(starDrawableEmpty)
            } else {
                star.setImageDrawable(createStarDrawable(emptyColor))
            }

            addView(star)
            stars.add(star)

            star.setOnClickListener {
                setRating(i.toFloat())
                animateStar(star)
            }
        }
        updateUI()
    }

    private fun createStarDrawable(color: Int): Drawable {
        val shape = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8f
            setColor(color)
            setStroke(4, strokeColor)
        }
        return shape
    }

    private fun updateUI() {
        for (i in 0 until starCount) {
            if (i < rating) {
                if (starDrawableFilled != 0)
                    stars[i].setImageResource(starDrawableFilled)
                else
                    stars[i].setImageDrawable(createStarDrawable(filledColor))
            } else {
                if (starDrawableEmpty != 0)
                    stars[i].setImageResource(starDrawableEmpty)
                else
                    stars[i].setImageDrawable(createStarDrawable(emptyColor))
            }
        }
    }

    private fun animateStar(view: View) {
        view.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(120)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).duration = 120
            }
    }

    fun setRating(value: Float) {
        rating = value
        updateUI()
        onRatingChange?.invoke(rating)
    }

    fun getRating(): Float = rating

    fun setOnRatingChangeListener(listener: (Float) -> Unit) {
        onRatingChange = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener() {
        setOnTouchListener { _, event ->
            val x = event.x
            val selected = ((x / (starSize + starGap)) + 1).toInt()

            if (selected in 1..starCount) {
                setRating(selected.toFloat())
            }
            true
        }
    }
}
