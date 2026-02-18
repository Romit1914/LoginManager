package com.yogitechnolabs.loginmanager.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import kotlin.math.abs

class SwipeDismissLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var startX = 0f
    private var translationXWhenDown = 0f
    private var isDismissed = false

    var dismissThreshold = 0.35f   // 35% of width
    var animationDuration = 200L

    var onDismiss: (() -> Unit)? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isDismissed) return false

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                startX = event.rawX
                translationXWhenDown = translationX
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - startX

                // ONLY right -> left swipe allowed
                if (dx < 0) {
                    translationX = translationXWhenDown + dx
                }
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                val dismissDistance = width * dismissThreshold

                if (abs(translationX) > dismissDistance) {
                    dismiss()
                } else {
                    resetPosition()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun dismiss() {
        animate()
            .translationX(-width.toFloat())
            .alpha(0f)
            .setDuration(animationDuration)
            .withEndAction {
                isDismissed = true
                visibility = GONE
                onDismiss?.invoke()
            }
            .start()
    }

    private fun resetPosition() {
        animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(animationDuration)
            .start()
    }
}
