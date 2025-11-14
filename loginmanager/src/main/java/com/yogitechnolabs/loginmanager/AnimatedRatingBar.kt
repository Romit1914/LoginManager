package com.yogitechnolabs.loginmanager

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class AnimatedRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var starCount = 5
    private var rating = 0f  // can be fractional like 3.5
    private var starSpacing = 20f

    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GRAY
    }
    private val starFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
    }

    private var starSize = 0f
    private var animationProgress = 0f
    private var animatingStarIndex = -1

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 300
        addUpdateListener {
            animationProgress = it.animatedValue as Float
            invalidate()
        }
        doOnEnd {
            animatingStarIndex = -1
        }
    }

    // Allow external set rating with animation
    fun setRatingAnimated(newRating: Float) {
        if (newRating < 0f || newRating > starCount) return
        animatingStarIndex = newRating.toInt() - 1
        rating = newRating
        animator.start()
    }

    fun setRating(newRating: Float) {
        if (newRating < 0f || newRating > starCount) return
        rating = newRating
        invalidate()
    }

    fun setStarCount(count: Int) {
        starCount = count
        invalidate()
    }

    fun setStarSpacing(spacing: Float) {
        starSpacing = spacing
        invalidate()
    }

    fun setStarColor(color: Int) {
        starFillPaint.color = color
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = 100
        val desiredWidth = ((desiredHeight * starCount) + (starSpacing * (starCount - 1))).toInt()

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
        starSize = (height * 0.8f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 0 until starCount) {
            val x = i * (starSize + starSpacing) + starSize / 2
            val y = height / 2f

            // Draw background star (gray)
            drawStar(canvas, x, y, starSize / 2, starPaint)

            // Calculate fill level for this star
            val fillLevel = when {
                i < rating.toInt() -> 1f
                i == rating.toInt() && rating % 1 > 0 -> rating % 1
                else -> 0f
            }

            // Animate fill for animating star
            val animatedFill = if (i == animatingStarIndex) fillLevel * animationProgress else fillLevel

            if (animatedFill > 0f) {
                drawPartialStar(canvas, x, y, starSize / 2, starFillPaint, animatedFill)
            }
        }
    }

    // Draw full star shape
    private fun drawStar(canvas: Canvas, cx: Float, cy: Float, radius: Float, paint: Paint) {
        val path = createStarPath(cx, cy, radius)
        canvas.drawPath(path, paint)
    }

    // Draw partially filled star (fillLevel 0.0 to 1.0)
    private fun drawPartialStar(canvas: Canvas, cx: Float, cy: Float, radius: Float, paint: Paint, fillLevel: Float) {
        val path = createStarPath(cx, cy, radius)

        canvas.save()
        canvas.clipRect(cx - radius, cy - radius, cx - radius + 2 * radius * fillLevel, cy + radius)
        canvas.drawPath(path, paint)
        canvas.restore()
    }

    // Star shape path
    private fun createStarPath(cx: Float, cy: Float, radius: Float): Path {
        val path = Path()
        val angle = Math.PI / 5.0
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) radius.toFloat() else radius.toFloat() / 2.5f

            val x = (cx + r * cos(i * angle - Math.PI / 2)).toFloat()
            val y = (cy + r * sin(i * angle - Math.PI / 2)).toFloat()

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        path.close()
        return path
    }
}
