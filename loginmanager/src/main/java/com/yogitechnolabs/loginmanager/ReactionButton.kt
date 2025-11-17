package com.yogitechnolabs.loginmanager

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kotlin.random.Random

class ReactionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mainButton: ImageView
    private val explosionContainer: FrameLayout

    // Reaction types: Emoji + icon resource id
    data class Reaction(val emoji: String, val iconResId: Int)

    private val reactions = listOf(
        Reaction("❤️", R.drawable.ic_heart),
        Reaction("💖", R.drawable.ic_love),
        Reaction("😂", R.drawable.ic_laugh),
        Reaction("😮", R.drawable.ic_wow)
    )

    private var selectedReactionIndex = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_reaction_button, this, true)
        mainButton = findViewById(R.id.mainButton)
        explosionContainer = findViewById(R.id.explosionContainer)

        updateMainButton()

        mainButton.setOnClickListener {
            playReactionAnimation()
        }
    }

    fun setReactions(list: List<Reaction>) {
        // Allow setting custom reactions if needed
        (reactions as MutableList).clear()
        reactions.addAll(list)
        selectedReactionIndex = 0
        updateMainButton()
    }

    fun setSelectedReaction(index: Int) {
        if (index in reactions.indices) {
            selectedReactionIndex = index
            updateMainButton()
        }
    }

    private fun updateMainButton() {
        val reaction = reactions[selectedReactionIndex]
        mainButton.setImageResource(reaction.iconResId)
        mainButton.contentDescription = "Reaction: ${reaction.emoji}"
    }

    private fun playReactionAnimation() {
        // Clear previous explosion views if any
        explosionContainer.removeAllViews()

        // Create multiple emoji bursts (say 6)
        val count = 6
        for (i in 0 until count) {
            val emojiView = TextView(context).apply {
                text = reactions[selectedReactionIndex].emoji
                textSize = 24f
                setTextColor(Color.RED)
                // Random start alpha for variation
                alpha = 1f
                x = mainButton.x + mainButton.width / 2f
                y = mainButton.y + mainButton.height / 2f
            }

            explosionContainer.addView(emojiView)

            // Animate emojiView flying outwards with fade out
            val angle = Math.toRadians((Random.nextInt(0, 360)).toDouble())
            val distance = Random.nextInt(100, 200).toFloat()

            val translationX = (distance * Math.cos(angle)).toFloat()
            val translationY = (distance * Math.sin(angle)).toFloat()

            val translateXAnim = ObjectAnimator.ofFloat(emojiView, "translationX", 0f, translationX)
            val translateYAnim = ObjectAnimator.ofFloat(emojiView, "translationY", 0f, translationY)
            val alphaAnim = ObjectAnimator.ofFloat(emojiView, "alpha", 1f, 0f)
            val scaleXAnim = ObjectAnimator.ofFloat(emojiView, "scaleX", 1f, 1.5f)
            val scaleYAnim = ObjectAnimator.ofFloat(emojiView, "scaleY", 1f, 1.5f)

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(translateXAnim, translateYAnim, alphaAnim, scaleXAnim, scaleYAnim)
            animatorSet.duration = 800
            animatorSet.startDelay = (i * 100).toLong()
            animatorSet.start()

            // Remove emojiView after animation
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                    explosionContainer.removeView(emojiView)
                }
            })
        }

        // Optionally: animate main button scale (pop)
        val scaleUpX = ObjectAnimator.ofFloat(mainButton, "scaleX", 1f, 1.3f)
        val scaleUpY = ObjectAnimator.ofFloat(mainButton, "scaleY", 1f, 1.3f)
        val scaleDownX = ObjectAnimator.ofFloat(mainButton, "scaleX", 1.3f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(mainButton, "scaleY", 1.3f, 1f)

        val scaleSet = AnimatorSet()
        scaleSet.play(scaleUpX).with(scaleUpY)
        scaleSet.play(scaleDownX).with(scaleDownY).after(scaleUpX)
        scaleSet.duration = 200
        scaleSet.start()
    }
}
